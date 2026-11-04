# Free command editor

`/rtp editor` creates a private, 24-hour web session for `config.yml`. It can be run from the console or by a player with `betterrtp.editor` (default: op), but no Discord login or premium. `/rtp editor cancel` revokes the current session and all its saved tokens; its creator or console may cancel it. One session is active per running server. Existing `/rtp edit` is unchanged.

## Setup and use

1. Deploy the companion website with PostgreSQL and apply its migrations using `pnpm db:deploy`. Protocol 2 requires migration `0002_editor_command_sync`. Set `APP_ORIGIN` to the deployed website origin. The editor API is available by default.
2. Install the matching plugin build and restart Minecraft. Normal builds target `https://minecentral.net`; staging builds target the test website below. There is no website setting or `editor.yml` resource.
3. From the console or as a permitted player, run `/rtp editor` and open the private link. The website displays `config.yml`; no server name or MOTD is sent.
4. Edit and click **Save and get sync command**. This stores an immutable version; nothing is applied to Minecraft yet.
5. Run `/betterrtp editorsync <token>` from the console or as a player with editor permission on the same server. The plugin downloads that version over HTTPS, checks permission again, validates it, backs up the current file, atomically replaces it, and reloads BetterRTP.

There are no inbound Minecraft connections and no automatic polling. Allow outbound HTTPS. TLS certificate validation remains enabled and redirects are refused. Never publish editor links or command tokens; Minecraft command logging plugins may capture sync commands.

## Expiry, versions, and errors

- Editor sessions expire 24 hours after creation without activity extending them. Each save has an independent 15-minute, single-use sync token. Saving again does not invalidate previous tokens, and syncing does not close the editor session.
- Expiring an editor stops new saves; existing save tokens retain their own deadlines. To replace the current session before its final tokens expire, its creator must use `/rtp editor cancel` first. Cancelling revokes all remaining tokens.
- Session expiry preserves the browser text for download. The personal config library and Discord login are not implemented yet.
- Tokens are consumed atomically when downloaded. If a response is lost, local validation fails, or the player disconnects before application, save again for a fresh token. The plugin does not retry writes. Minecraft reports the apply outcome; the website does not report generated commands as applied.
- The player running editorsync must still be online and hold `betterrtp.editor` at apply time. Restarting/disabling the plugin discards its in-memory session, so old tokens cannot be applied by that plugin instance after restart.
- Invalid content is rejected before any file change. Validation preserves the existing setting structure and types, checks selected numeric ranges, blocks Language-File changes and unsafe Bukkit YAML constructs, and limits config size to 128 KiB. This is conservative validation, not a complete schema for every integration.
- Intervening edits to the Minecraft config do not block syncing: applying a saved version can overwrite those edits. A backup of the current file is created in `plugins/BetterRTP/editor-backups/`; rotate backups as appropriate.
- If writing succeeds but reload fails, the new file stays in place and a reload error is reported. There is no automatic rollback. Check Minecraft's console and reload after resolving the failure.
- The private plugin-session capability binds redemption to the original session; no server label is collected or used for authorization.

Protocol 1 automatic-apply clients are intentionally incompatible with protocol 2. The website migration closes existing protocol 1 sessions. Update website/database and plugin together for testing.

## Production and staging builds

Run `mvn clean package` for `target/BetterRTP-3.6.13.jar`, targeting `https://minecentral.net`.
Run `mvn clean package -Pstaging` for `target/BetterRTP-3.6.13-staging.jar`, targeting
`https://n2nm1thopoojau1ggnalixvr.minecentral.net`.

The address is internal JAR build metadata, not a server configuration or environment setting. Upload only one BetterRTP JAR to the plugins directory. Use the staging website origin as its `APP_ORIGIN` and apply the database migration there.

## Validation

Run `mvn -f tests/editor/pom.xml test` for configuration validation/file-write tests and `mvn package -Pstaging` for the full staging plugin. The web repository's `pnpm test` exercises version isolation, atomic single-use redemption, server binding, independent expiry, cancellation, validation, and limits against embedded PostgreSQL.

Before production use, test the complete flow on the staging Minecraft server, including permission denial, permission loss, disconnect, expiry, repeated saves, intervening local changes, reload errors, restart, and Folia scheduling if applicable. Local automated checks do not substitute for this staging run.
