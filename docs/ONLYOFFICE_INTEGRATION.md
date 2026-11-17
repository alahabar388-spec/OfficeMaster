# ONLYOFFICE integration plan

## Target architecture

Office Mastermy Android client must not contain a Document Server secret or JWT signing key. The Android app authenticates to a backend over HTTPS. The backend creates an editor session and returns a short-lived editor configuration to the client.

Required components:

1. ONLYOFFICE Docs Document Server, reachable by both the browser/client and the backend.
2. HTTPS backend API with user authentication and document-level authorization.
3. Private document storage. The backend creates a time-limited download URL for the editor.
4. ONLYOFFICE callback endpoint. It validates the callback and downloads the edited file from the Document Server before atomically saving a new document version.
5. Android editor screen. It loads the configured ONLYOFFICE editor URL in a WebView only after the backend has supplied a validated configuration.

## Proposed backend API

- POST /api/documents/{id}/editor-session
  - Authenticated request from the Android client.
  - Response: document key, editor config, Document Server editor URL, and expiration.
- GET /api/documents/{id}/download
  - Authorized, short-lived document download.
- POST /api/onlyoffice/callback/{documentId}
  - Server-to-server callback from Document Server.
  - Validate JWT/signature, validate document ownership, download saved content, persist a new version, and return the required callback response.
- GET /api/documents/{id}/versions
  - List saved versions.

## Security requirements

- HTTPS only.
- Keep Document Server JWT secret exclusively on the backend.
- Never trust document IDs, callback URLs, or download URLs supplied by the Android client without authorization.
- Use short-lived signed download URLs.
- Validate callback JWT/signature and callback status before downloading.
- Enforce file size/type limits and per-user access checks.
- Do not expose private storage paths.

## Current status

The Android project currently has a document picker and hands selected files to compatible installed apps. The ONLYOFFICE editor is not connected because no Document Server URL, backend API, authentication method, or storage service has been configured yet.

## Next implementation gate

Before enabling the editor screen, provision a reachable ONLYOFFICE Docs server and backend, then provide the HTTPS base URLs and chosen authentication flow. Do not place credentials or JWT secrets in the APK or Git repository.
