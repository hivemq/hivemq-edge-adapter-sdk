# UI Schema Configuration Guide

**Version:** 1.0
**Audience:** Java developers building protocol adapters for HiveMQ Edge

---

## Table of Contents

1. [Overview](#1-overview)
2. [Standard RJSF Options](#2-standard-rjsf-options)
3. [HiveMQ Edge Extensions](#3-hivemq-edge-extensions)
4. [Tab Layout Configuration](#4-tab-layout-configuration)
5. [Array Configuration](#5-array-configuration)
6. [Widget Reference](#6-widget-reference)
7. [Format Reference](#7-format-reference)
8. [Common Mistakes](#8-common-mistakes)
9. [Best Practices](#9-best-practices)

---

## 1. Overview

### What is UI Schema?

UI Schema controls how the HiveMQ Edge frontend renders configuration forms. While JSON Schema defines the data structure and validation rules, UI Schema determines visual presentation and user interaction.

| Schema | Purpose |
|--------|---------|
| **JSON Schema** | Data structure, validation, and field metadata |
| **UI Schema** | Widget selection, field ordering, and layout |

### How UI Schema Works with JSON Schema

The HiveMQ Edge frontend uses [React JSON Schema Form (RJSF)](https://rjsf-team.github.io/react-jsonschema-form/docs/) to render configuration forms. RJSF reads both schemas and generates the appropriate UI:

1. JSON Schema provides field types, validation rules, and metadata
2. UI Schema overrides default widget choices and configures layout
3. RJSF merges both schemas to render the final form

### UI Schema File Location

Place UI Schema files in your adapter's resources directory:

```
src/main/resources/<adapter-name>-ui-schema.json
```

**Example:** For a Hello World adapter, create:

```
src/main/resources/helloworld-adapter-ui-schema.json
```

### Loading UI Schema in Java

The JSON file must be loaded and returned by the `getUiSchema()` method in your `ProtocolAdapterInformation` implementation:

```java
@Override
public @Nullable String getUiSchema() {
    try (final InputStream is = this.getClass()
            .getClassLoader()
            .getResourceAsStream("helloworld-adapter-ui-schema.json")) {
        if (is == null) {
            LOG.warn("The UISchema for the Hello World Adapter could not be loaded from resources: Not found.");
            return null;
        }
        return IOUtils.toString(is, StandardCharsets.UTF_8);
    } catch (Exception e) {
        LOG.warn("The UISchema for the Hello World Adapter could not be loaded from resources:", e);
        return null;
    }
}
```

**Required imports:**

```java
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
```

If `getUiSchema()` returns `null`, the frontend renders the form using default RJSF behavior based on the JSON Schema alone.

---

## 2. Standard RJSF Options

The HiveMQ Edge frontend supports standard RJSF UI Schema options. For complete documentation, see the [official RJSF UI Schema reference](https://rjsf-team.github.io/react-jsonschema-form/docs/api-reference/uiSchema).

### Common Standard Options

#### ui:widget

Specifies the input widget for a field.

```json
{
  "port": {
    "ui:widget": "updown"
  },
  "password": {
    "ui:widget": "password"
  },
  "description": {
    "ui:widget": "textarea"
  }
}
```

#### ui:order

Defines the display order of fields.

```json
{
  "ui:order": ["id", "host", "port", "timeout", "*"]
}
```

The `*` wildcard includes all remaining fields not explicitly listed.

#### ui:disabled

Disables a field (prevents editing).

```json
{
  "readOnlyField": {
    "ui:disabled": true
  }
}
```

**Important:** Do NOT hardcode `ui:disabled` for the `id` field. The frontend sets this dynamically based on create versus edit mode.

#### ui:readonly

Makes a field read-only (similar to disabled but with different styling).

```json
{
  "calculatedValue": {
    "ui:readonly": true
  }
}
```

#### ui:placeholder

Adds placeholder text to input fields.

```json
{
  "host": {
    "ui:placeholder": "e.g., 192.168.1.100"
  }
}
```

#### ui:help

Provides additional help text below a field.

```json
{
  "certificatePath": {
    "ui:help": "Path to the PEM-encoded certificate file"
  }
}
```

---

## 3. HiveMQ Edge Extensions

The HiveMQ Edge frontend extends standard RJSF with custom options for protocol adapter configuration.

### Quick Reference Tables

#### Custom UI Options

| Option | Purpose | Location |
|--------|---------|----------|
| `ui:tabs` | Tab-based layout for grouping fields | Root level |
| `ui:initTab` | Initial tab to display on form load | Root level |
| `ui:batchMode` | Enable batch upload for array fields | Array field |
| `ui:collapsable` | Make array items collapsible | Array field |

#### Custom Widgets

| Widget | Purpose | Use Case |
|--------|---------|----------|
| `updown` | Numeric stepper input | Port fields, bounded numbers |
| `discovery:tagBrowser` | Tag browser interface | Tag selection fields |
| `application/schema+json` | JSON Schema editor | Schema editing interfaces |

#### Custom Fields

| Field | Purpose |
|-------|---------|
| `mqtt:transform` | MQTT transformation configuration |
| `compactTable` | Compact array display format |

#### Custom Formats

| Format | Validation |
|--------|------------|
| `mqtt-topic` | MQTT topic (no wildcards, no null characters) |
| `mqtt-topic-filter` | MQTT topic filter (wildcards allowed) |
| `mqtt-tag` | Tag name validation |
| `identifier` | Adapter identifier pattern |
| `jwt` | JWT token format |

---

## 4. Tab Layout Configuration

Group related configuration fields into tabs for improved usability.

### Basic Tab Configuration

```json
{
  "ui:tabs": [
    {
      "id": "connection",
      "title": "Connection",
      "properties": ["id", "host", "port", "timeout"]
    },
    {
      "id": "security",
      "title": "Security",
      "properties": ["enableTls", "certificate", "privateKey"]
    },
    {
      "id": "subscriptions",
      "title": "Subscriptions",
      "properties": ["subscriptions"]
    }
  ],
  "ui:initTab": "connection"
}
```

### Tab Configuration Properties

| Property | Type | Required | Description |
|----------|------|----------|-------------|
| `id` | string | Yes | Unique identifier for the tab |
| `title` | string | Yes | Display label for the tab |
| `properties` | string[] | Yes | Field names to include in this tab |

### Setting the Initial Tab

Use `ui:initTab` to specify which tab displays when the form opens.

```json
{
  "ui:tabs": [
    { "id": "basic", "title": "Basic Settings", "properties": ["id", "host"] },
    { "id": "advanced", "title": "Advanced", "properties": ["timeout", "retries"] }
  ],
  "ui:initTab": "basic"
}
```

### Complete Tab Example

```json
{
  "ui:tabs": [
    {
      "id": "connection",
      "title": "Connection",
      "properties": ["id", "host", "port"]
    },
    {
      "id": "authentication",
      "title": "Authentication",
      "properties": ["username", "password"]
    },
    {
      "id": "mappings",
      "title": "Data Mappings",
      "properties": ["subscriptions"]
    }
  ],
  "ui:initTab": "connection",
  "port": {
    "ui:widget": "updown"
  },
  "password": {
    "ui:widget": "password"
  },
  "subscriptions": {
    "ui:batchMode": true,
    "ui:collapsable": {
      "titleKey": "mqttTopic"
    }
  }
}
```

---

## 5. Array Configuration

Configure how array fields display and behave in the form.

### Batch Mode

Enable batch upload functionality for array fields. Batch mode provides a bulk import option for adding multiple items at once.

```json
{
  "subscriptions": {
    "ui:batchMode": true
  }
}
```

### Collapsible Arrays

Make array items collapsible to reduce visual clutter with long lists.

```json
{
  "subscriptions": {
    "ui:collapsable": {
      "titleKey": "mqttTopic"
    }
  }
}
```

The `titleKey` property specifies which field value displays as the collapsed item title.

### Collapsible Configuration Properties

| Property | Type | Required | Description |
|----------|------|----------|-------------|
| `titleKey` | string | Yes | Property name to use as the collapsed item title |

### Combined Array Configuration

Use both options together for optimal user experience.

```json
{
  "subscriptions": {
    "ui:batchMode": true,
    "ui:collapsable": {
      "titleKey": "mqttTopic"
    },
    "items": {
      "mqttTopic": {
        "ui:placeholder": "e.g., sensors/temperature"
      },
      "pollingInterval": {
        "ui:widget": "updown"
      }
    }
  }
}
```

### Array Item Widget Configuration

Configure widgets for fields within array items using the `items` property.

```json
{
  "dataPoints": {
    "ui:batchMode": true,
    "items": {
      "address": {
        "ui:placeholder": "Enter register address"
      },
      "dataType": {
        "ui:widget": "select"
      }
    }
  }
}
```

---

## 6. Widget Reference

### Standard Widgets

| Widget | Input Type | Use Case |
|--------|------------|----------|
| `text` | Single-line text | Default for strings |
| `textarea` | Multi-line text | Long descriptions, JSON content |
| `password` | Masked text | Credentials, secrets |
| `select` | Dropdown | Enum fields |
| `checkbox` | Checkbox | Boolean fields |
| `radio` | Radio buttons | Small enum sets |
| `hidden` | Hidden input | Internal fields |

### HiveMQ Custom Widgets

#### updown

Numeric stepper with increment/decrement buttons.

```json
{
  "port": {
    "ui:widget": "updown"
  }
}
```

**Best for:** Port numbers, polling intervals, numeric thresholds.

#### discovery:tagBrowser

Tag browser interface for selecting tags from a device.

```json
{
  "tagAddress": {
    "ui:widget": "discovery:tagBrowser"
  }
}
```

**Note:** This widget currently renders as a standard text input. Future versions provide a browseable tag selection interface.

#### application/schema+json

JSON Schema editor for fields that accept schema definitions.

```json
{
  "transformSchema": {
    "ui:widget": "application/schema+json"
  }
}
```

**Best for:** Configuration fields that define data transformation schemas.

### Widget Selection Guidelines

| Field Type | Recommended Widget |
|------------|-------------------|
| Password/Secret | `password` |
| Port number | `updown` |
| Timeout (numeric) | `updown` |
| Long text/JSON | `textarea` |
| Enum (3+ options) | `select` |
| Enum (2 options) | `radio` |
| Boolean | `checkbox` |
| Tag selection | `discovery:tagBrowser` |

---

## 7. Format Reference

Custom formats provide HiveMQ-specific validation for field values.

### MQTT Formats

#### mqtt-topic

Validates MQTT topic strings. Rejects wildcards (`#`, `+`) and null characters.

```json
{
  "publishTopic": {
    "type": "string",
    "format": "mqtt-topic"
  }
}
```

**Valid:** `sensors/temperature`, `devices/device-01/status`
**Invalid:** `sensors/#`, `devices/+/status`

#### mqtt-topic-filter

Validates MQTT topic filters. Allows wildcards for subscription patterns.

```json
{
  "subscriptionFilter": {
    "type": "string",
    "format": "mqtt-topic-filter"
  }
}
```

**Valid:** `sensors/#`, `devices/+/status`, `sensors/temperature`
**Invalid:** `sensors/##`, `devices/++/status`

#### mqtt-tag

Validates tag name strings for MQTT-based tagging.

```json
{
  "tagName": {
    "type": "string",
    "format": "mqtt-tag"
  }
}
```

### Identifier Format

#### identifier

Validates adapter and resource identifiers. Allows alphanumeric characters, hyphens, and underscores.

```json
{
  "adapterId": {
    "type": "string",
    "format": "identifier"
  }
}
```

**Valid:** `my-adapter-01`, `sensor_device_1`
**Invalid:** `my adapter`, `device@01`

### Authentication Format

#### jwt

Validates JWT (JSON Web Token) format strings.

```json
{
  "authToken": {
    "type": "string",
    "format": "jwt"
  }
}
```

---

## 8. Common Mistakes

This section documents real issues found in HiveMQ Edge adapters with corrected examples.

### Mistake 1: Hardcoding ui:disabled for ID Field

**Problem:** Setting `ui:disabled: true` on the ID field.

**Impact:** Users cannot enter an ID when creating a new adapter instance.

**Before (Incorrect):**

```json
{
  "id": {
    "ui:disabled": true
  }
}
```

**After (Correct):**

Remove the `ui:disabled` setting. The frontend handles ID field state dynamically.

```json
{
  "id": {
    "ui:placeholder": "Enter a unique adapter ID"
  }
}
```

### Mistake 2: Missing Password Widget

**Problem:** Password fields render as plain text inputs.

**Impact:** Credentials display in clear text, creating security concerns.

**Before (Incorrect):**

```json
{
  "password": {}
}
```

**After (Correct):**

```json
{
  "password": {
    "ui:widget": "password"
  }
}
```

### Mistake 3: Missing Tab Configuration

**Problem:** All fields appear in a single long form without grouping.

**Impact:** Users struggle to navigate complex adapter configurations.

**Before (Incorrect):**

```json
{
  "host": {},
  "port": {},
  "username": {},
  "password": {},
  "subscriptions": {}
}
```

**After (Correct):**

```json
{
  "ui:tabs": [
    {
      "id": "connection",
      "title": "Connection",
      "properties": ["host", "port"]
    },
    {
      "id": "auth",
      "title": "Authentication",
      "properties": ["username", "password"]
    },
    {
      "id": "data",
      "title": "Data Mappings",
      "properties": ["subscriptions"]
    }
  ],
  "ui:initTab": "connection"
}
```

### Mistake 4: Missing Collapsible Configuration for Arrays

**Problem:** Long subscription lists display fully expanded.

**Impact:** Users cannot easily navigate or manage large configuration arrays.

**Before (Incorrect):**

```json
{
  "subscriptions": {}
}
```

**After (Correct):**

```json
{
  "subscriptions": {
    "ui:collapsable": {
      "titleKey": "mqttTopic"
    }
  }
}
```

### Mistake 5: Text Input for Port Fields

**Problem:** Port fields use default text input instead of numeric stepper.

**Impact:** Users can enter non-numeric values; no quick increment/decrement.

**Before (Incorrect):**

```json
{
  "port": {}
}
```

**After (Correct):**

```json
{
  "port": {
    "ui:widget": "updown"
  }
}
```

### Mistake 6: Missing titleKey in Collapsible Arrays

**Problem:** Collapsible arrays show generic "Item 1, Item 2" labels.

**Impact:** Users cannot identify array items without expanding each one.

**Before (Incorrect):**

```json
{
  "subscriptions": {
    "ui:collapsable": true
  }
}
```

**After (Correct):**

```json
{
  "subscriptions": {
    "ui:collapsable": {
      "titleKey": "mqttTopic"
    }
  }
}
```

### Mistake 7: Generic Tab Names

**Problem:** Tabs named "Tab 1", "Tab 2", or similar generic labels.

**Impact:** Users cannot identify tab content without clicking each one.

**Before (Incorrect):**

```json
{
  "ui:tabs": [
    { "id": "tab1", "title": "Tab 1", "properties": ["host", "port"] },
    { "id": "tab2", "title": "Tab 2", "properties": ["username", "password"] }
  ]
}
```

**After (Correct):**

```json
{
  "ui:tabs": [
    { "id": "connection", "title": "Connection", "properties": ["host", "port"] },
    { "id": "authentication", "title": "Authentication", "properties": ["username", "password"] }
  ]
}
```

### Mistake 8: Missing Field Order

**Problem:** Fields display in random order determined by JSON Schema generation.

**Impact:** Related fields appear separated; logical flow disrupted.

**Before (Incorrect):**

```json
{
  "host": {},
  "timeout": {},
  "port": {}
}
```

**After (Correct):**

```json
{
  "ui:order": ["host", "port", "timeout", "*"],
  "host": {},
  "port": {},
  "timeout": {}
}
```

---

## 9. Best Practices

### Do's

#### Use Tabs for Complex Configurations

Group related fields into logical tabs:

```json
{
  "ui:tabs": [
    { "id": "connection", "title": "Connection", "properties": ["id", "host", "port"] },
    { "id": "security", "title": "Security", "properties": ["enableTls", "certificate"] },
    { "id": "mappings", "title": "Data Mappings", "properties": ["subscriptions"] }
  ],
  "ui:initTab": "connection"
}
```

#### Use Appropriate Widgets for Field Types

Match widgets to field semantics:

```json
{
  "port": { "ui:widget": "updown" },
  "password": { "ui:widget": "password" },
  "notes": { "ui:widget": "textarea" }
}
```

#### Enable Batch Mode for Subscription Arrays

Improve bulk data entry experience:

```json
{
  "subscriptions": {
    "ui:batchMode": true
  }
}
```

#### Make Long Arrays Collapsible

Reduce visual clutter for arrays with many items:

```json
{
  "subscriptions": {
    "ui:collapsable": {
      "titleKey": "mqttTopic"
    }
  }
}
```

#### Define Field Order Explicitly

Ensure logical field sequence:

```json
{
  "ui:order": ["id", "host", "port", "username", "password", "subscriptions", "*"]
}
```

#### Provide Placeholders for Complex Fields

Guide users with example values:

```json
{
  "host": {
    "ui:placeholder": "e.g., 192.168.1.100 or plc.example.com"
  },
  "mqttTopic": {
    "ui:placeholder": "e.g., sensors/device-01/temperature"
  }
}
```

### Don'ts

#### Do Not Hardcode ui:disabled for ID Fields

The frontend manages ID field state dynamically:

```json
// Wrong
{
  "id": {
    "ui:disabled": true
  }
}

// Correct - omit ui:disabled for id
{
  "id": {
    "ui:placeholder": "Enter a unique adapter ID"
  }
}
```

#### Do Not Use Default Text Input for Passwords

Always mask password fields:

```json
// Wrong
{
  "password": {}
}

// Correct
{
  "password": {
    "ui:widget": "password"
  }
}
```

#### Do Not Leave Arrays Without Collapse Configuration

Long arrays need collapsible display:

```json
// Wrong
{
  "subscriptions": {}
}

// Correct
{
  "subscriptions": {
    "ui:collapsable": {
      "titleKey": "mqttTopic"
    }
  }
}
```

#### Do Not Use Generic Tab Names

Use descriptive tab titles:

```json
// Wrong
{ "id": "tab1", "title": "Tab 1", "properties": [...] }

// Correct
{ "id": "connection", "title": "Connection", "properties": [...] }
```

#### Do Not Omit Field Order

Random field order confuses users:

```json
// Wrong - no ui:order
{
  "host": {},
  "port": {}
}

// Correct
{
  "ui:order": ["id", "host", "port", "*"],
  "host": {},
  "port": {}
}
```

---

## Quick Reference Checklist

Use this checklist before submitting adapter UI Schema for review.

### Structure

- [ ] UI Schema file exists in `src/main/resources/uiSchema/`
- [ ] `ui:tabs` groups related fields logically
- [ ] `ui:order` defines logical field sequence
- [ ] Tab names are clear and descriptive (not "Tab 1", "Tab 2")

### Widgets

- [ ] Port fields use `ui:widget: "updown"`
- [ ] Password fields use `ui:widget: "password"`
- [ ] Multi-line content uses `ui:widget: "textarea"`
- [ ] Bounded numeric fields use `ui:widget: "updown"`

### Field Behavior

- [ ] `id` field does NOT have hardcoded `ui:disabled`
- [ ] Read-only fields have `ui:readonly: true`

### Arrays and Objects

- [ ] Array mappings use `ui:batchMode: true`
- [ ] Collapsible arrays have `ui:collapsable` with `titleKey`
- [ ] Array items have appropriate widget configuration

---

## Complete Example

This example shows a complete UI Schema for a protocol adapter.

```json
{
  "ui:tabs": [
    {
      "id": "connection",
      "title": "Connection",
      "properties": ["id", "host", "port", "timeout"]
    },
    {
      "id": "authentication",
      "title": "Authentication",
      "properties": ["username", "password", "enableTls", "certificate"]
    },
    {
      "id": "subscriptions",
      "title": "Data Mappings",
      "properties": ["subscriptions"]
    }
  ],
  "ui:initTab": "connection",
  "ui:order": [
    "id",
    "host",
    "port",
    "timeout",
    "username",
    "password",
    "enableTls",
    "certificate",
    "subscriptions",
    "*"
  ],
  "id": {
    "ui:placeholder": "Enter a unique adapter ID"
  },
  "host": {
    "ui:placeholder": "e.g., 192.168.1.100"
  },
  "port": {
    "ui:widget": "updown"
  },
  "timeout": {
    "ui:widget": "updown"
  },
  "password": {
    "ui:widget": "password"
  },
  "certificate": {
    "ui:widget": "textarea",
    "ui:placeholder": "Paste PEM-encoded certificate"
  },
  "subscriptions": {
    "ui:batchMode": true,
    "ui:collapsable": {
      "titleKey": "mqttTopic"
    },
    "items": {
      "mqttTopic": {
        "ui:placeholder": "e.g., sensors/device-01/temperature"
      },
      "pollingInterval": {
        "ui:widget": "updown"
      }
    }
  }
}
```

---

## Related Documentation

- [JSON Schema Configuration Guide](./JSON_SCHEMA_CONFIGURATION_GUIDE.md) - Field definition and validation reference
- [Protocol Adapter QA Checklist](./ADAPTER_QA_CHECKLIST.md) - Complete quality assurance checklist
- [RJSF UI Schema Documentation](https://rjsf-team.github.io/react-jsonschema-form/docs/api-reference/uiSchema) - Official React JSON Schema Form reference
