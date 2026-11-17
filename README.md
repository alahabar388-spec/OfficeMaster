# Office Mastermy

Android document hub built with Kotlin.

## First source milestone
- Native Android project and Gradle configuration.
- Local document picker using Android Storage Access Framework.
- File handoff to installed document viewers for DOC/DOCX, XLS/XLSX, PPT/PPTX, PDF, CSV and text.
- Share action for the selected document.
- Print handoff through a compatible viewer or print-enabled app.
- GitHub Actions debug APK build.

## Important limitations
This milestone is a document launcher, not a full Office suite. Offline rendering and editing of every format are provided by compatible applications installed on the device; this app does not bundle an offline Office rendering engine.

ONLYOFFICE online editing is not yet active. A production integration requires an accessible ONLYOFFICE Docs Document Server plus a backend that securely issues editor configuration, serves document URLs and handles save callbacks. Do not put server secrets or signing keys in the Android APK.

## Build
Open GitHub Actions and run the workflow named Build Office Mastermy APK. Download the OfficeMastermy-APK artifact from the completed workflow run.
