# Plugin web editors

BetterRTP and BetterRTP Addons share the form/YAML editor, private config library, 24-hour uploads, and single-use 15-minute sync commands.

| Plugin | Open server config | Apply saved config | Permission (default: op) | Website |
| --- | --- | --- | --- | --- |
| BetterRTP | `/rtp editor` | `/betterrtp editorsync <token>` | `betterrtp.editor` | `/editor/betterrtp` |
| BetterRTP Addons | `/rtp addons editor` | `/betterrtp addons editorsync <token>` | `betterrtp.addon.editor` | `/editor/betterrtp-addons` |

Console can use both commands. Addons sync backs up and replaces **BetterRTPAddons/config.yml**, then reloads the addons. It does not write BetterRTP's config or addon databases. Addons uses the editor bridge and website build metadata from the installed BetterRTP JAR, so install both updated JARs together.

## Addons deployment

1. Deploy the website with `pnpm db:deploy` to apply `0017_addons_editor` before serving the updated editor API. Existing tokens default to BetterRTP. The migration binds the existing `betterrtp-addons` catalog listing to its stable editor identity; its ID, release history, and premium classification remain unchanged. Later slug renames retain editor support.
2. Build from the BetterRTP repository: `mvn clean install`, then `mvn -f BetterRTPAddons/pom.xml clean package`. The JARs are in `target/` and `BetterRTPAddons/target/`. The Addons `dev` profile optionally copies its JAR to the local plugins directory.
3. Install both JARs and restart the Minecraft server. Open `/rtp addons editor`, edit a setting, save online, and run the returned command. Check Minecraft's apply confirmation and the Addons `editor-backups` folder.

Uploads, sync payloads, login recovery drafts, and My Configs are scoped to their plugin. Redeeming an Addons token using BetterRTP's command is rejected without consuming it. Existing expiry/cleanup rules apply to both plugins. The editor is free even when the catalog plugin requires premium to download.

The Addons form supports feature toggles, command/lore lists, menu worlds, and flashback warning times/messages. Validation preserves fixed keys/types, blocks Language-File changes, and checks menu lines/slots, timers, and stick quantities. The receiving server also validates materials and the logger date format. Quoted Minecraft color codes are accepted while YAML tags, aliases, and Bukkit object serialization remain blocked.

Run `mvn -f tests/editor/pom.xml test` and the website's `pnpm test`, `pnpm typecheck`, and `pnpm db:validate`. Automated checks cover default Addons YAML, numeric warning keys, collection edits, backups, plugin isolation, one-use sync, migration binding, and separate snapshot allowances. A live Minecraft upload/apply/reload still needs a server smoke test.

## BetterRTP command editor

`/rtp editor` uploads `config.yml` and loaded world names, then returns an independent eight-character editor token valid for 24 hours. Run the command from console or with `betterrtp.editor` (default: op). No login or premium is required. There is no active-editor slot, cancellation command, or plugin-controlled token lifetime. Running the command again creates another independent upload. `/rtp edit` is unchanged.

## Use and refresh

1. Run `/rtp editor` and open `/editor/betterrtp?token=<code>`.
2. Edit the form or YAML, then click **Save and get sync command**. This updates the upload's latest config/world suggestions while its token is valid, and creates an immutable sync payload.
3. Run `/betterrtp editorsync <code>` on the originating Minecraft server. Console or any player with current editor permission can apply it.
4. Refreshing a valid token restores its last successful save. Shared tokens use last-save-wins behavior. Refreshing an invalid/expired token removes it from the URL, shows a dismissible warning toast, and opens the default config.

Uploads expire after 24 hours without extension. An open editor can continue generating public sync commands after expiry and cleanup. Refreshing an expired token opens defaults with a warning. The browser warns on refresh/tab close for unsaved changes and expired upload tokens. Download is available to recover local edits.

## Sync authorization and retention

Sync commands can be created from the public editor, a local YAML upload, My Configs, or an in-game upload link. No editor token, receipt, login, or server credential is required to generate/redeem a protocol-4 command. Anyone holding the command can run it on a compatible server with `betterrtp.editor` permission (or console access). The plugin validates against the receiving server’s current config before writing. Previously generated `editor-server.key` files are no longer used.

The editor token only provides 24-hour upload loading and latest-save refresh persistence. Saving without a valid token still generates an independent sync command; it does not create a new reloadable upload. No signed receipt or browser credential is needed.

Each sync command expires after 15 minutes and is consumed atomically when downloaded. Different saves retain their own exact contents; applying an older command may overwrite newer changes. Upload expiry/deletion does not delete an independent sync payload. The plugin does not automatically apply changes, poll the website, or expose an inbound port.

If download fails after consumption, save again for a new command. Minecraft reports the apply result; the website does not claim that a generated command was applied. Permission and player presence are checked again before writing. Validation failure makes no config changes. A backup of the current file is kept in `editor-backups`; an atomic replacement is followed by reload. Reload failure leaves the newly written config in place and reports an error, without automatic rollback.

Validation preserves fixed setting names/types, allows supported collection edits, checks effective ranges, blocks Language-File changes and unsafe Bukkit YAML constructs, and limits configs to 128 KiB. Intervening local edits do not block application. World availability is advisory: manually editing suggestions does not create/delete Minecraft worlds. The immutable reported list contains loaded worlds at upload time; run `/rtp editor` again for an updated list.

## Deployment and cleanup

Deploy the website, run `pnpm db:deploy` for migration `0007_calm_whirlwind`, then install the matching protocol-4 JAR. No additional environment variable is required. Generate fresh editor tokens after updating the JAR; legacy protocol-2/3 sync commands are not interchangeable with protocol 4. The website retains legacy reads/sync support until those records expire, but does not expose a plugin cancellation endpoint.

Schedule `cd /app && pnpm db:editor-cleanup` daily in Coolify (`0 0 * * *`). It deletes expired uploads and expired/consumed sync payloads independently. Legacy expired records are removed after their remaining sync tokens expire. Personal configs are unaffected. Legacy signing-key storage is unused and retained for migration compatibility; database backups have their own retention policy.

Normal builds use `https://www.minecentral.net`; the staging profile uses the URL in `pom.xml`. The address comes from JAR build metadata, not a server configuration. Build with `mvn clean package` or `mvn clean package -Pstaging`; install only one JAR. TLS validation stays enabled and redirects are refused. Keep editor URLs, sync-token headers out of logs/analytics.

## Verification

Run `mvn -f tests/editor/pom.xml test` for Java validation/file-write checks, and `pnpm test` in the website for database/token behavior. Test the deployed browser/Minecraft flow with multiple uploads, refresh, invalid tokens, saved world suggestions, restart, one-use sync, permission loss, and reload failure. Local checks do not replace this live verification.

### Save dialog

Both the public editor and uploaded-config editor provide **Save and get sync command**. A successful save opens a native modal dialog with steps for applying the config, the exact command, a copy button with confirmation/manual fallback, and its expiry time. Nothing is applied until someone runs the command in Minecraft. Commands remain single-use for 15 minutes; each applies its exact saved version.

Deploy the updated website and plugin together. No additional migration is required beyond `0007_calm_whirlwind`. The plugin no longer generates or needs `editor-server.key`. Public editor saves intentionally work without first running `/rtp editor`.

### Departure protection

Public and token editors share the same YAML/form renderer and departure guard. The guard compares YAML text and world suggestions against the loaded reference (or the latest successfully persisted save for a valid upload token). Searching, switching form/YAML views, and untouched loading do not count as edits. Reverting both YAML and world suggestions clears the warning. Public sync saves do not make the page reloadable, so they do not clear protection; expired tokens also cannot provide refresh recovery for edited content.

Reload, closing the tab, and full-page navigation use the browser's native warning. Site links use Next.js `onNavigate` to ask before client-side navigation; download links and opening links in another tab do not interrupt the editor. Discord sign-in deliberately preserves a temporary recovery draft before navigating. Same-document browser Back/Forward and arbitrary programmatic routing are not intercepted by this guard. Mobile browser termination can bypass native unload warnings.

The public/default and database-token loading wrappers remain separate; the renderer, sync dialog, change comparison, and departure protection are shared.
