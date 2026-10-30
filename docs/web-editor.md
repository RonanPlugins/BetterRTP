# Free command editor

`/rtp editor` opens a temporary web session for the existing `config.yml`. It does not register the server, require a premium plan, or require Discord. `/rtp editor cancel` stops the active session. Permission: `betterrtp.editor` (default: op), including permission to write config and reload BetterRTP. Existing `/rtp edit` is unchanged.

## Setup

1. Deploy the companion BetterRTP-Webapp with PostgreSQL. Apply its committed Drizzle migrations, set `EDITOR_ENABLED=true`, and set `APP_ORIGIN=https://minecentral.net`.
2. Build/install this plugin. The editor website is fixed to `https://minecentral.net`; there is no website setting or `editor.yml` resource. Any old editor.yml file is ignored.
3. Run `/rtp editor`. Open the private chat link, edit the YAML, and choose **Save and apply to server**. Download is also available. A player who started the session must remain online with permission when applying; console-created sessions use console authority.
4. The UI first shows pending. It shows applied only after the plugin has validated, backed up, written, reloaded, verified the loaded/persisted settings, and acknowledged the result.

No inbound ports on the Minecraft server are needed. The plugin calls the website over HTTPS every three seconds while a session is active. Outbound HTTPS must be permitted. Certificate checks remain enabled, and redirects are refused.

## Limits and failure handling

- One active editor per running server; fifteen-minute expiry and one submitted save. Start a fresh session for further edits. Cancelling cannot undo a write already in progress.
- Only `config.yml`, at most 128 KiB. Economy, database, language and other files are not exposed. Changing `Language-File` is blocked.
- Existing setting names and structured world/group entries must remain. Values and scalar lists may change. Numeric ranges and setting types are checked, with conservative limits for preload radius and attempts. YAML tags, anchors and aliases are rejected. The conservative parser also rejects literal equals signs and encoded character escapes to prevent Bukkit object-deserialization markers; edit such unusual configs locally. This is structural validation plus selected semantic checks, not a version-complete schema for every integration.
- A file changed since the session snapshot is rejected. Do not edit the file externally during apply: external processes do not participate in the plugin's lock/checks.
- Backups are written to `plugins/BetterRTP/editor-backups/` before replacement. Maintain/rotate these backups as appropriate for your server.
- Atomic replacement is required. Reload exceptions or a detected reload mismatch restore the original file and attempt another reload. If that reload also fails, restart the server; plugins/integrations may have partially changed in-memory state.
- Network failure retries until expiry. After a local apply result, only the acknowledgement is retried, never the write. A crash/restart cancels the in-memory session; it never resumes applying queued edits. If an acknowledgement is lost or the plugin crashes after writing, the website can show pending until expiry: inspect the live config and server messages before starting over.
- Anyone with the browser link can submit its one edit until it expires. Permission is checked at creation and again before applying. Do not share links, paste them into public chat, or run them through third-party link tools. Browser tokens are in URL fragments; plugin tokens never reach the browser.

The website is trusted with the submitted config; this protocol is not end-to-end encrypted against the hosting operator. Restrict DB access and avoid putting secrets in the main config. The one-time session grants no persistent premium server access.

## Validation

Run `mvn -f tests/editor/pom.xml test` for the isolated config validator/file-write suite. It uses the actual Spigot YAML implementation without requiring all of the plugin's protection integrations. Run `mvn package` for the full plugin. Before production use, test the complete flow on a staging Minecraft server, including permissions, disconnect, reload failure and Folia scheduling if applicable.
