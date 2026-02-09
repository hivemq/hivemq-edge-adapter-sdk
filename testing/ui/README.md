# HiveMQ Edge Adapter Visual Testing Suite

A self-contained testing tool that allows Java adapter developers to visually test their protocol adapter configuration forms without running the full HiveMQ Edge application.

## Overview

This testing suite provides:

- **Visual form preview** - See exactly how your adapter configuration form will render
- **JSON Schema validation** - Verify your `@ModuleConfigField` annotations generate correct schemas
- **UI Schema testing** - Test tab layouts, field ordering, and widget configurations
- **Real-time validation** - Test required fields, patterns, and constraint validation

## Quick Start (From Clean Clone)

### Option A: Run directly with an adapter JAR

This is the fastest way to test an adapter without modifying its build file.

```bash
# 1. Clone and build the testing suite
cd hivemq-edge-adapter-sdk/testing/ui
./gradlew build

# 2. Build your adapter (example: hello-world adapter)
cd /path/to/hivemq-hello-world-protocol-adapter
./gradlew shadowJar

# 3. Run the test server with your adapter JAR
cd /path/to/hivemq-edge-adapter-sdk/testing/ui
./gradlew run -PadapterJar=/path/to/hivemq-hello-world-protocol-adapter/build/libs/hivemq-hello-world-protocol-adapter-1.0-SNAPSHOT-all.jar
```

The browser opens automatically to http://localhost:8080

**Available URLs:**
- `http://localhost:8080/` - Full view with adapter info, schema tabs, form data
- `http://localhost:8080/form` - Form only (clean view for testing)
- `http://localhost:8080/form/{adapterId}` - Form for specific adapter type

### Option B: Add testUI task to your adapter project

For repeated use, add a Gradle task to your adapter's build file:

```kotlin
// build.gradle.kts
dependencies {
    testImplementation("com.hivemq:hivemq-edge-adapter-sdk-testing-ui:1.0.0")
}

tasks.register<JavaExec>("testUI") {
    group = "verification"
    description = "Launch visual UI test server for adapter configuration"
    mainClass.set("com.hivemq.edge.adapters.testing.AdapterTestServer")
    classpath = sourceSets.main.get().runtimeClasspath +
                configurations.testRuntimeClasspath.get()
}
```

Then simply run:

```bash
./gradlew testUI
```

## Complete Example: Hello World Adapter

Starting from a clean clone:

```bash
# 1. Clone the SDK repo (contains the testing suite)
git clone https://github.com/hivemq/hivemq-edge-adapter-sdk.git
cd hivemq-edge-adapter-sdk

# 2. Build the testing suite
cd testing/ui
./gradlew build

# 3. Clone and build the hello-world adapter
cd ../..
git clone https://github.com/hivemq/hivemq-hello-world-protocol-adapter.git
cd hivemq-hello-world-protocol-adapter
./gradlew shadowJar

# 4. Run the test server
cd ../hivemq-edge-adapter-sdk/testing/ui
./gradlew run -PadapterJar=../../hivemq-hello-world-protocol-adapter/build/libs/hivemq-hello-world-protocol-adapter-1.0-SNAPSHOT-all.jar

# 5. Browser opens automatically to http://localhost:8080
#    - Go to http://localhost:8080/form for clean form view
```

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Developer's Machine                       │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────────┐       ┌──────────────────────────┐   │
│  │  Adapter JAR     │       │   Testing Suite          │   │
│  │  (on classpath)  │       │                          │   │
│  │                  │       │  ┌──────────────────┐    │   │
│  │  - Config class  │◄──────┤  │  Java Server     │    │   │
│  │  - UI Schema     │       │  │  (JDK HttpServer)│    │   │
│  │  - Factory       │       │  │  Port 8080       │    │   │
│  └──────────────────┘       │  └────────┬─────────┘    │   │
│                             │           │              │   │
│                             │  ┌────────▼─────────┐    │   │
│                             │  │  React App       │    │   │
│                             │  │  (RJSF + Chakra) │    │   │
│                             │  └──────────────────┘    │   │
│                             └──────────────────────────┘   │
│                                                              │
│  Browser: http://localhost:8080                             │
└─────────────────────────────────────────────────────────────┘
```

## How It Works

1. **Adapter Discovery** - The Java server uses ServiceLoader to find your `ProtocolAdapterFactory` implementation
2. **Schema Generation** - JSON Schema is generated from your config class annotations
3. **UI Schema Loading** - The `getUiSchema()` method from your `ProtocolAdapterInformation` is called
4. **Form Rendering** - The React app renders your configuration form using RJSF

## Requirements

### Adapter JAR must contain:

1. **ServiceLoader registration**:
   ```
   META-INF/services/com.hivemq.adapter.sdk.api.factories.ProtocolAdapterFactory
   ```

2. **Config class with annotations**:
   ```java
   public class MyAdapterConfig implements ProtocolSpecificAdapterConfig {
       @JsonProperty("id")
       @ModuleConfigField(
           title = "Identifier",
           description = "Unique identifier for this adapter instance",
           required = true,
           format = FieldType.IDENTIFIER
       )
       private String id;
       // ...
   }
   ```

3. **Optional UI Schema file** in resources, loaded via `getUiSchema()`:
   ```java
   @Override
   public @Nullable String getUiSchema() {
       try (InputStream is = getClass().getClassLoader()
               .getResourceAsStream("my-adapter-ui-schema.json")) {
           return IOUtils.toString(is, StandardCharsets.UTF_8);
       } catch (Exception e) {
           return null;
       }
   }
   ```

## Supported Features

### JSON Schema

- Field titles and descriptions
- Required fields
- Number constraints (min/max)
- String constraints (minLength/maxLength/pattern)
- Enum values with display names
- Custom formats (mqtt-topic, identifier, etc.)

### UI Schema

- **ui:tabs** - Tab-based form layout
- **ui:order** - Field ordering
- **ui:widget** - Widget selection (updown, password, textarea)
- **ui:disabled** / **ui:readonly** - Field state

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `PORT` or `-Dserver.port` | 8080 | HTTP server port |
| `FRONTEND_PATH` or `-Dfrontend.path` | frontend/dist | Path to React app build |

### Example with custom port:

```bash
./gradlew testUI -Dserver.port=9090
```

## Development

### Prerequisites

- **Java 21+** - For the backend server
- **Node.js 18+** - Only needed if modifying the React frontend

### Building from Source

The React frontend is pre-built and committed to `frontend/dist/`. You only need to rebuild it if you modify the frontend code.

```bash
# Build everything (Java + copies pre-built frontend)
./gradlew build

# If you modified the React app, rebuild it first:
cd frontend
npm install
npm run build
cd ..
./gradlew build
```

### Running in Development Mode

For active frontend development with hot reload:

**Terminal 1** - Java server (serves API on port 8080):
```bash
# Auto-detects JAR in build/libs/, or specify explicitly
./gradlew run
# or
./gradlew run -PadapterJar=/path/to/your-adapter.jar
```

**Terminal 2** - React dev server with hot reload (port 5173, proxies API to 8080):
```bash
cd frontend
npm run dev
```

Then open http://localhost:5173 for hot-reloading frontend development.

### Running Cypress Tests

The test suite includes automated E2E tests based on the [Adapter QA Checklist](../docs/ADAPTER_QA_CHECKLIST.md).

```bash
cd frontend

# First, ensure the test server is running (in another terminal):
# cd /path/to/testing/ui && ./gradlew run -PadapterJar=/path/to/adapter.jar

# Run tests interactively
npm run cypress:open

# Run tests headless (CI mode)
npm run cypress:run
```

## Automated Tests

The Cypress tests are organized by QA checklist sections:

### Test Files

| File | QA Section | Description |
|------|------------|-------------|
| `1-json-schema.cy.ts` | Section 1 | JSON Schema validation tests |
| `2-ui-schema.cy.ts` | Section 2 | UI Schema validation tests |
| `6-visual-testing.cy.ts` | Section 6 | Visual/browser-based tests |

### Test Categories

#### Section 1: JSON Schema Validation
- **[1.1.x] Field Metadata** - Titles, descriptions, formatting
- **[1.2.x] Type Constraints** - Number/string constraint validation
- **[1.3.x] Required Fields** - Required field configuration
- **[1.4.x] Enum Fields** - Enum display values
- **[1.5.x] Format Types** - Field format validation

#### Section 2: UI Schema Validation
- **[2.1.x] Structure** - ui:tabs, ui:order configuration
- **[2.2.x] Widgets** - Widget assignments (updown, password)
- **[2.3.x] Field Behavior** - Disabled/readonly states
- **[2.4.x] Arrays** - Array field configuration

#### Section 6: Visual Testing
- **[6.1.x] Form Rendering** - No JS errors, all fields visible
- **[6.2.x] Validation** - Required fields, bounds checking
- **[6.3.x] Accessibility** - Labels, keyboard navigation

### Running Specific Tests

```bash
# Run only JSON Schema tests
npm run cypress:run -- --spec "cypress/e2e/1-json-schema.cy.ts"

# Run only UI Schema tests
npm run cypress:run -- --spec "cypress/e2e/2-ui-schema.cy.ts"

# Run only Visual tests
npm run cypress:run -- --spec "cypress/e2e/6-visual-testing.cy.ts"
```

### Custom Commands

The test suite provides custom Cypress commands:

```typescript
// Visit the form page and wait for adapter data
cy.visitForm()              // Auto-selects first adapter
cy.visitForm('my-adapter')  // Select specific adapter by ID

// Get adapter data from API
cy.getFirstAdapter()        // Get the first/only adapter
cy.getAdapters()            // Get all adapters

// Form interactions
cy.submitForm()             // Click submit button
cy.getFieldByLabel('Port')  // Get field by its label text
```

## CI Pipeline / QA Reports

### Using Gradle (Recommended)

Run the full QA pipeline with a single Gradle command:

```bash
# From testing/ui directory
./gradlew qaCheck
```

The task auto-detects adapter JARs in `build/libs/*-all.jar`. If auto-detection fails, specify the JAR explicitly:

```bash
./gradlew qaCheck -PadapterJar=/path/to/your-adapter.jar
```

This will:
1. Start the test server with your adapter
2. Run all Cypress tests
3. Generate the QA report
4. Stop the server

**Available Gradle tasks:**

| Task | Description |
|------|-------------|
| `./gradlew qaCheck` | Full QA pipeline (auto-detects JAR in build/libs/) |
| `./gradlew qaCheck -PadapterJar=...` | Full QA pipeline with explicit JAR path |
| `./gradlew qaReport` | View the last generated report |
| `./gradlew testUI` | Interactive mode (start server for manual testing) |

### Using npm directly

```bash
cd frontend

# Ensure test server is running first (in another terminal)
# Run QA checks and generate report
npm run qa:check
```

This will:
1. Run all Cypress tests in headless mode
2. Generate `cypress/results.json` with test results
3. Generate a formatted report with:
   - Pass/fail status for each checklist item
   - Rationale explaining why each check matters
   - Suggested fixes for failures
   - Severity classification (critical/high/medium/low)

### Sample Report Output

```
================================================================================
ADAPTER QA REPORT
================================================================================

Generated: 2024-01-21T18:30:00.000Z

## Summary

Total Tests: 38
Passed: 35
Failed: 3

Failures by Severity:
  🔴 Critical: 1
  🟠 High: 2

## Failed Checks (3)

### 🔴 [1.2.1] Integer fields use number constraints

**Why this matters:** Using string constraints on integer fields causes validation
to fail silently or produce confusing errors.

**How to fix:** Use `numberMin` and `numberMax` instead of `stringMinLength`
and `stringMaxLength` for integer fields.

---
```

### GitHub Actions Integration

Example workflow for adapter repositories:

```yaml
# .github/workflows/qa.yml
name: Adapter QA

on: [pull_request]

jobs:
  qa:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '20'

      - name: Build adapter
        run: ./gradlew shadowJar

      - name: Checkout testing suite
        uses: actions/checkout@v4
        with:
          repository: hivemq/hivemq-edge-adapter-sdk
          path: sdk

      - name: Install npm dependencies
        run: |
          cd sdk/testing/ui/frontend
          npm ci

      - name: Run QA checks
        run: |
          cd sdk/testing/ui
          ./gradlew qaCheck -PadapterJar=${{ github.workspace }}/build/libs/*-all.jar

      - name: Upload report
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: qa-report
          path: |
            sdk/testing/ui/frontend/qa-report.json
            sdk/testing/ui/frontend/cypress/results/
```

## Troubleshooting

### "No adapter types found"

- Ensure your adapter JAR is on the classpath
- Check that `META-INF/services/com.hivemq.adapter.sdk.api.factories.ProtocolAdapterFactory` exists
- Verify the file contains your factory class's fully qualified name

### Form doesn't render correctly

- Check browser console for JavaScript errors
- Verify JSON Schema is valid (see "JSON Schema" tab in the UI)
- Check UI Schema syntax (see "UI Schema" tab)

### UI Schema tabs not working

- Ensure `ui:tabs` array is at the root level of UI Schema
- Verify property names in tabs match JSON Schema property names exactly

## Related Documentation

- [JSON Schema Configuration Guide](../docs/JSON_SCHEMA_CONFIGURATION_GUIDE.md)
- [UI Schema Configuration Guide](../docs/UI_SCHEMA_CONFIGURATION_GUIDE.md)
- [Adapter QA Checklist](../docs/ADAPTER_QA_CHECKLIST.md)
