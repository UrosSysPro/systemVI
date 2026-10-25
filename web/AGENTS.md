# AGENTS.md

Instructions for AI agents working in this repository (`web/`). Human contributors
should find most of it useful too.

## What this is

An sbt multi-module Scala 3 (3.3.3) project with two apps:

- **`main_menu/`** — a restaurant menu system (the active app).
- **`website/`** — the older marketing site (mostly untouched; use it as a style reference).

Each app has three modules: `common` (shared DTOs, cross-compiled), `server` (JVM),
`client` (Scala.js). `main_menu` is the one we work on.

Stack: http4s + doobie + SQLite + circe + log4cats + ciris + decline-effect (server);
Laminar + Airstream + Waypoint + circe + Scala.js, bundled by Vite + Tailwind (client).

## Non-negotiable working rules

1. **Verify with tests, never against a running dev server.** When you need to check
   behaviour, write/run a test (MUnit + `munit-cats-effect` for `mainMenuServer`).
   **Do not call `http://localhost:8080`** (or any live endpoint, or start the dev
   server just to probe it). HTTP routes are tested in-process in
   `mainMenuServer/src/test/.../api/routes/ApiRoutesTest.scala`.
2. **Do not commit build output or databases.** `target/`, `node_modules/`, `*.db`
   and SQLite sidecar files are ignored via per-module `.gitignore` files.
3. **Never delete historical migrations.** The runner keeps a `SchemaMigrations`
   table; removing an applied migration breaks `migrate down`. To undo a schema
   change, add a new `__NN_...` migration.
4. **Match existing patterns** (layering, naming, DTO style, view style) rather than
   introducing new ones. Keep changes surgical.
5. **Docker image builds are human-executed.** When a Docker build or publish is
   needed, print the exact sbt command (e.g.
   `sbt mainMenuServer/docker:publishLocal`) and stop. Wait for the user to run it and
   confirm whether it worked. Do not run `docker:publishLocal`/`docker:publish`
   yourself, and do not try to work around Docker daemon or socket permission errors.

## Environment notes

This project is developed on two machines. Read the subsection for the one you are
on; ignore the other.

### Windows 11 (msys2)

- Shell is PowerShell. `&&` is not supported — use `;`. Avoid `$` in terminal commands
  (they get rejected); use literal paths.
- `git` is **not on PATH**: use the full path `C:\msys64\usr\bin\git.exe`. The git repo
  root is the **parent** of `web/` (`C:\Users\uros\Projects\SystemVI`), so pathspecs are
  relative to that, e.g. `git -C C:\Users\uros\Projects\SystemVI ls-files -- "web/**/*.db"`.
  Use `--no-pager` for read-only git.

### Arch Linux

- Shell is bash. `&&` and `$VAR` work normally.
- `git` **is** on `PATH`. The git repo root is the **parent** of `web/`
  (`/home/uros/Projects/SystemVI`), so pathspecs are relative to that, e.g.
  `git -C /home/uros/Projects/SystemVI ls-files -- "web/**/*.db"`. Use `--no-pager` for
  read-only git.
- Toolchain: `sbt` via coursier (`~/.local/share/coursier/bin/sbt`), Java 21 (GraalVM,
  via sdkman; the Docker images use `eclipse-temurin:25`), Node 24, both `bun` and `npm`.
- The committed client lockfile is `bun.lock`, so prefer `bun install` here.

## Key commands

These work in both shells; the only per-OS differences are the ones noted above.

```sh
# from web/

# server: compile, test, run
sbt mainMenuServer/compile
sbt mainMenuServer/test
sbt mainMenuServer/run                 # start HTTP server (port 8080, api under /api)

# database (decline CLI)
sbt "mainMenuServer/run migrate"        # apply pending migrations
sbt "mainMenuServer/run migrate --force" # DELETE the db and run every migration from scratch
sbt "mainMenuServer/run migrate down"    # revert the most recently applied migration
sbt "mainMenuServer/run seed"            # seed default data
sbt "mainMenuServer/run config"          # print loaded config

# client
sbt mainMenuClient/compile
sbt mainMenuClient/fastLinkJS            # links to main_menu/client/target/.../mainmenuclient-fastopt/main.js
# dev server:
cd main_menu/client; npm install; npm run dev   # http://localhost:5174 (Arch: bun install; bun run dev)
```

`migrate --force` deletes `main_menu/server/db/main_menu.db` before opening a
transactor — it fails (with a friendly message) if the server still holds the file,
so stop the server first.

## Server conventions

Layering is strict and one file per concern:

```
persistance/models/        case classes (the domain)
persistance/migrations/    __NN_description.scala (BaseMigration subclasses) + Migrations.scala registry
persistance/mappings/      SqlMappings.scala — doobie Read instances
persistance/contexts/      SQL (doobie) per entity; DatabaseContext wires them into AppContext
persistance/seeders/       seeders + Seeders.scala orchestrator
services/                  thin business layer over contexts
api/controllers/           http4s HttpRoutes
api/routes/Api.scala       Router mapping path prefixes -> controllers
config/                    ciris config (server.json, google-client-secret.json, jwt-auth.json)
Main.scala                 decline-effect CLI (serve/migrate/seed/config)
```

`main_menu/common` holds DTOs in `net.systemvi.mainmenu.common.dto`.

### Adding/changing an entity (checklist)

1. Model in `persistance/models`.
2. Migration: new `__NN_...(xa)` class extending `BaseMigration[F]`, register it in
   `Migrations.all`. **Never edit an already-applied migration.**
3. `Read[...]` in `SqlMappings` — tuple order must match the table's column order.
4. Context (CRUD SQL), then service, then controller, then route in `Api.scala`.
5. DTO in `common` and map model -> DTO in the controller.
6. Tests: context CRUD (`TestDatabase.transactor`) and, for new routes, `ApiRoutesTest`.

### Database / migration rules

- SQLite tables are named **plural** (`Items`, `Categories`, `Restaurants`, `RestaurantTables`).
- UUIDs are stored as text: `given Read[UUID] = Read[String].map(UUID.fromString)`
  and `given Write[UUID] = Write[String].contramap(_.toString)`. `Option[UUID]` works.
- **SQLite cannot add a `NOT NULL` column to a populated table without a default**, so
  new columns are added as nullable and modelled as `Option[...]` (e.g.
  `Item.categoryId`, `Category.restaurantId`).
- Prefer explicit column lists in `select` (not `select *`) so adding columns is safe.
- No FK constraints are declared/enforced.
- Controllers: `GET Root` (list), `GET Root / UUIDVar(id)` (one or 404), `POST Root`,
  `PUT Root / UUIDVar(id)`, `DELETE Root / UUIDVar(id)`. POST/PUT/DELETE return the
  number of affected rows / id as text.

### circe gotcha (important)

`io.circe.generic.auto.*` derivation **cannot handle recursive DTOs** (e.g.
`CategoryDto.children: List[CategoryDto]`) — it blows past `-Xmax-inlines`. Also, an
`auto` import in lexical scope **shadows** companion codecs. So:

- Recursive DTOs (`CategoryDto`) have **hand-written `Encoder`/`Decoder` in their companion**.
- For non-recursive DTOs, define companion codecs with `semiauto.deriveEncoder/deriveDecoder`
  (see `RestaurantDto`, `RestaurantTableDto`).
- Do **not** import `io.circe.generic.auto.*` in a file that encodes a recursive DTO;
  use explicit `semiauto` givens for request bodies instead.

## Client conventions (`main_menu/client`)

- `routes/Pages.scala` — the `Page` ADT. `routes/Routes.scala` — Waypoint routes
  (`segment[UUID]`, tuple params for multi-segment; include `/ endOfSegments` on this
  Waypoint version). `routes/Router.scala` — manual string history serialization.
  `Main.scala` — `SplitRender` wiring page -> view.
- Views: public pages in `views/`, admin pages in `views/admin/`.
- Shared UI: `components/Layout.scala` (`pageShell`, `pageColumn`, `pageTitle`,
  `listColumn`, `linkRow`, `itemCard`); `components/AdminLayout.scala` (`adminLayout`
  collapsible side nav, and the `reloadable` helper for fetch + refresh);
  `components/AdminForms.scala` (`styledInput`, `actionButton`, `reactiveActionButton`,
  `formRow`, `optionalText`).
- HTTP: `api/MenuApi.scala` (public reads), `api/AdminApi.scala` (POST/PUT/DELETE +
  admin reads), `api/AuthApi.scala` (session: `currentUser`/`session`, `googleSignInUrl`).
  Base URL in `utils/Constants.scala` (`http://localhost:8080/api` in dev).
- **Styling uses inline Laminar CSS props** (`display.flex`, `padding.rem(1)`, `flex("1")`,
  …), not Tailwind classes — keep it that way so views don't depend on the CSS build.
- After a write, refresh lists by emitting on the page's `EventBus` fed into `reloadable`.
- **The client has no test framework.** Verify by `mainMenuClient/compile` and
  `mainMenuClient/fastLinkJS` (produces the bundle Vite loads). Do not spin up the browser.

### Routes currently in the app

Auth:
- `/login` — shared sign-in page (Google) for admins, waiters and customers
- `/welcome` — post sign-in landing; customers are sent to the menu, staff pick a restaurant

Public (read-only menu):
- `/` — restaurant picker
- `/restaurant/{restaurantId}`
- `/restaurant/{restaurantId}/category/{categoryId}`
- `/restaurant/{restaurantId}/category/{categoryId}/subcategory/{subcategoryId}`

Admin (collapsible side nav):
- `/admin/restaurant/{restaurantId}/dashboard` (embedded centered menu)
- `/admin/restaurant/{restaurantId}/tables`
- `/admin/restaurant/{restaurantId}/categories`
- `/admin/restaurant/{restaurantId}/categories/{categoryId}` (its subcategories + items)

### API surface (under `/api`)

- `GET /restaurants`, `GET /restaurants/{id}`, `GET /restaurants/{id}/categories`
  (that restaurant's category tree), `GET /restaurants/{id}/tables`
- `GET /category` (forest), `GET /category/{id}` (subtree with subcategories),
  `POST /category`, `PUT /category/{id}`, `DELETE /category/{id}`
- `GET /items`, `GET /items/{id}`, `GET /items/category/{id}`,
  `POST /items`, `PUT /items/{id}`, `DELETE /items/{id}`
- `GET /restaurant-tables`, `GET /restaurant-tables/{id}`,
  `POST /restaurant-tables`, `PUT /restaurant-tables/{id}`, `DELETE /restaurant-tables/{id}`
- Auth: `GET /auth/google/redirect` (302 to Google), `GET /auth/google/callback`
  (exchanges the code, sets the HttpOnly `access_token` cookie, 302 to the client `/welcome`),
  `GET /user` (returns the session `UserDto`; behind JWT auth, reads the cookie).
- CORS middleware allows `localhost` and credentials.

### Authentication

Google-only sign-in, mirroring `website/server` (with cleanups: no secret in URLs,
no dead services, real error statuses):

- `api/controllers/GoogleAuthController.scala` — `/redirect` and `/callback`.
- `services/GoogleUriService.scala` — builds the Google URLs and the registered
  callback; `services/GoogleApiService.scala` — token exchange + userinfo (deps
  injected, so tests stub it instead of hitting the network).
- `services/JwtService.scala` — mints HS256 session tokens; `api/middleware/`
  `CookieToAuthorizationMiddleware` copies the cookie to a Bearer header and
  `JwtAuthMiddleware` verifies it into a `common.dto.UserDto`.
- Credentials are read from gitignored files `config/google-client-secret.json`
  (`web.client_id` / `web.client_secret`) and `config/jwt-auth.json` (`secret`);
  copy the `.example` files. The OAuth callback is derived from
  `ServerConfig.serverUrl` (`/api/auth/google/callback`) — register that exact URL
  with Google.
- First Google sign-in creates an `ACTIVE` `CUSTOMER` `User` plus an
  `AuthIdentities` row; later sign-ins with the same Google `sub` reuse the user.
  (Staff accounts, anonymous users and merging are not implemented yet.)

## Domain model

```
User      *─1 Role (lookup)                 User.email links staff to Google sign-in
User      1─* AuthIdentity (Google sub -> user)
Restaurant 1─* RestaurantTable
Restaurant 1─* Category (self-nesting via parentId; max 2 levels in practice)
Category   1─* Item
```

Everything is scoped per restaurant and **never shared**: the same dish served in two
restaurants is two separate `Item` rows. Items belong to categories (`categoryId`),
categories belong to restaurants (`restaurantId`), tables belong to restaurants.

`RestaurantSeeders` seeds two restaurants, each with 3 tables, 5 top-level categories
(2 of which have 2 subcategories each) and 5–10 items per category.

## When in doubt

- Copy the closest existing feature end-to-end (`Item`/`Category`/`RestaurantTable` are
  good templates) and keep the diff minimal.
- Run `sbt mainMenuServer/test` (and `mainMenuClient/fastLinkJS` if you touched the
  client) before finishing. Report exactly what you ran.
