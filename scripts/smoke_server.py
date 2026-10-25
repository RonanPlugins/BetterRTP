"""Boot a pinned Paper/Folia server with the same built plugin artifacts."""
import argparse
import hashlib
import json
from pathlib import Path
import queue
import re
import shutil
import subprocess
import sys
import threading
import time
import urllib.request
import zipfile

def download(url, path, digest, algorithm="sha256"):
    if path.exists() and hashlib.new(algorithm, path.read_bytes()).hexdigest() == digest:
        return
    request = urllib.request.Request(url, headers={"User-Agent": "BetterRTP CI (https://github.com/RonanPlugins/BetterRTP)"})
    with urllib.request.urlopen(request, timeout=120) as response:
        data = response.read()
    if hashlib.new(algorithm, data).hexdigest() != digest:
        raise RuntimeError("Checksum mismatch for " + url)
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(data)

def main():
    sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    parser = argparse.ArgumentParser()
    parser.add_argument("--server-url", required=True)
    parser.add_argument("--sha256", required=True)
    parser.add_argument("--label", required=True)
    parser.add_argument("--java", default="java")
    parser.add_argument("--directory", required=True)
    parser.add_argument("--client-version")
    args = parser.parse_args()
    root = Path(__file__).resolve().parents[1]
    directory = Path(args.directory).resolve()
    directory.mkdir(parents=True, exist_ok=True)
    (directory / "plugins").mkdir(exist_ok=True)
    download(args.server_url, directory / "server.jar", args.sha256)
    # Paperclip declares the exact upstream jar and checksum in its own metadata.
    with zipfile.ZipFile(directory / "server.jar") as archive:
        if "META-INF/download-context" in archive.namelist():
            checksum, url, filename = archive.read("META-INF/download-context").decode().strip().split()
            download(url, directory / "cache" / filename, checksum)
    shutil.copy2(root / "target/BetterRTP-3.6.13.jar", directory / "plugins/BetterRTP.jar")
    (directory / "eula.txt").write_text("eula=true\n")
    (directory / "server.properties").write_text("\n".join((
        "allow-nether=false", "enable-query=false", "enable-rcon=false", "enable-status=false",
        "generate-structures=false", "level-name=world", "online-mode=false", "difficulty=peaceful",
        "level-type=minecraft:flat",
        'generator-settings={"layers":[{"block":"minecraft:bedrock","height":1},{"block":"minecraft:dirt","height":2},{"block":"minecraft:grass_block","height":1}],"biome":"minecraft:plains"}',

        "server-ip=127.0.0.1", "server-port=25585", "simulation-distance=2", "spawn-protection=0", "view-distance=2",
    )) + "\n")
    if args.client_version:
        plugin_dir = directory / "plugins/BetterRTP"
        plugin_dir.mkdir(exist_ok=True)
        config = (root / "src/main/resources/config.yml").read_text()
        config = config.replace("Enabled: false # Make the player rtp", "Enabled: true # Make the player rtp")
        config = config.replace("DisableUpdater: false", "DisableUpdater: true")
        config = config.replace("PreloadRadius: 5", "PreloadRadius: 1")
        config = config.replace("Time: 5", "Time: 2").replace("MinY: 0", "MinY: -64")
        config = config.replace("MaxRadius: 1000", "MaxRadius: 100").replace("MinRadius: 10", "MinRadius: 40")
        (plugin_dir / "config.yml").write_text(config)
        shutil.copy2(root / "src/main/resources/effects.yml", plugin_dir / "effects.yml")
    process = subprocess.Popen([args.java, "-Xms512M", "-Xmx1G", "-jar", "server.jar", "--nogui"],
        cwd=directory, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
        text=True, encoding="utf-8", errors="replace", bufsize=1)
    lines = queue.Queue()
    def read_output():
        for line in process.stdout:
            lines.put(line)
        lines.put(None)
    threading.Thread(target=read_output, daemon=True).start()
    deadline = time.monotonic() + 240
    command_at = stop_at = None
    client = None
    client_log = None
    forced = False
    username = "Compat" + str(int(time.time()))[-8:]
    log = []
    timed_out = False
    try:
        while True:
            now = time.monotonic()
            if now >= deadline:
                timed_out = True
                process.kill()
                break
            if command_at is not None and now >= command_at:
                process.stdin.write("plugins\nbrtp version\nbrtp info\nbrtp info potion_effects\nbrtp info particles\n")
                process.stdin.flush()
                command_at = None
                if args.client_version:
                    client_log = open(directory / "client.log", "w", encoding="utf-8")
                    client = subprocess.Popen(["node", str(root / "scripts/test_client.cjs"), args.client_version, username],
                        stdout=client_log, stderr=subprocess.STDOUT)
                else:
                    stop_at = now + 8
            if client is not None:
                client_output = (directory / "client.log").read_text(encoding="utf-8", errors="replace")
                if not forced and "You have been tp'd" in client_output:
                    process.stdin.write("brtp player " + username + " world IGNORECOOLDOWN NOCOOLDOWN" + chr(10))
                    process.stdin.flush()
                    forced = True
                if client.poll() is not None and stop_at is None:
                    stop_at = now
            if stop_at is not None and now >= stop_at:
                process.stdin.write("stop\n")
                process.stdin.flush()
                stop_at = float("inf")
            try:
                line = lines.get(timeout=0.25)
            except queue.Empty:
                continue
            if line is None:
                break
            print(line, end="", flush=True)
            log.append(line)
            if "Done (" in line:
                command_at = time.monotonic() + 2
        code = process.wait(timeout=30)
    finally:
        if process.poll() is None:
            process.kill()
        process.wait()
        if client is not None and client.poll() is None:
            client.kill()
            client.wait()
        if client_log is not None:
            client_log.close()
    output = re.sub(r"\x1b\[[0-9;]*m", "", "".join(log))
    (directory / "smoke.log").write_text(output, encoding="utf-8")
    required = ("Enabling BetterRTP v", "[BetterRTP] Version #", "----- BetterRTP | Info -----")
    forbidden = ("Error occurred while enabling BetterRTP", "Thread ownership", "UnsupportedClassVersionError",
        "NoSuchMethodError", "NoSuchFieldError", "IncompatibleClassChangeError", "Could not pass event", "Cannot add effects",
        "Unsupported API version", "Exception", "Your MC version isn't supported")
    missing = [item for item in required if item not in output]
    found = [item for item in forbidden if item in output]
    result = {"label": args.label, "return_code": code, "timed_out": timed_out, "missing": missing, "forbidden": found}
    if args.client_version:
        result["client_return_code"] = client.returncode if client is not None else None
        if client_log is not None:
            print((directory / "client.log").read_text(encoding="utf-8", errors="replace"))
    print(json.dumps(result), flush=True)
    if code != 0 or timed_out or missing or found or (args.client_version and result["client_return_code"] != 0):
        sys.exit(1)

if __name__ == "__main__":
    main()
