# JSON Schema Configuration Guide

**Version:** 1.0
**Audience:** Java developers building protocol adapters for HiveMQ Edge

---

## Table of Contents

1. [Overview](#1-overview)
2. [The @ModuleConfigField Annotation](#2-the-moduleconfigfield-annotation)
3. [Field Types and Constraints](#3-field-types-and-constraints)
4. [Common Mistakes](#4-common-mistakes)
5. [Best Practices](#5-best-practices)

---

## 1. Overview

### What is JSON Schema Configuration?

HiveMQ Edge protocol adapters use JSON Schema to define their configuration structure. The HiveMQ Edge frontend renders configuration forms dynamically based on these schemas using the React JSON Schema Form (RJSF) library.

The configuration system consists of two documents:

| Document | Purpose |
|----------|---------|
| **JSON Schema** | Defines configuration structure, validation rules, and field metadata |
| **UI Schema** | Controls visual presentation, widget selection, and field ordering |

### How Schema Generation Works

1. You define configuration fields in Java using `@JsonProperty` and `@ModuleConfigField` annotations
2. The HiveMQ Edge backend generates JSON Schema from these annotations at runtime
3. The frontend receives the schema via the `/api/v1/management/protocol-adapters/types` endpoint
4. RJSF renders the configuration form based on both schemas

### Why Correct Configuration Matters

Incorrect schema configuration causes:

- **Validation failures** - Users enter invalid data that the backend rejects
- **Poor user experience** - Missing labels, confusing field names, or incorrect widgets
- **Runtime errors** - Type mismatches between frontend and backend
- **Security issues** - Password fields displayed as plain text

---

## 2. The @ModuleConfigField Annotation

The `@ModuleConfigField` annotation is the primary tool for configuring how fields appear in the generated JSON Schema and the frontend form.

**Location:** `com.hivemq.adapter.sdk.api.annotations.ModuleConfigField`

### Complete Attribute Reference

```java
@ModuleConfigField(
    // User-facing metadata
    title = "Field Title",              // Label displayed in the form
    description = "Help text",          // Tooltip or help text

    // Validation
    required = true,                    // Mark field as required
    defaultValue = "default",           // Default value (as string)
    format = FieldType.HOSTNAME,        // Format validation type

    // Numeric constraints (for Integer, Long, Double fields)
    numberMin = 1,                      // Minimum value (inclusive)
    numberMax = 65535,                  // Maximum value (inclusive)

    // String constraints (for String fields only)
    stringMinLength = 1,                // Minimum character count
    stringMaxLength = 1024,             // Maximum character count
    stringPattern = "^[a-zA-Z0-9]*$",   // Regex pattern for validation

    // Array constraints (for List/array fields)
    arrayMinItems = 1,                  // Minimum array length
    arrayMaxItems = 100,                // Maximum array length
    arrayUniqueItems = true,            // Require unique items

    // Enum display
    enumDisplayValues = {"Display 1", "Display 2"},  // User-friendly enum labels

    // Access control
    readOnly = false,                   // Prevent editing
    writeOnly = false                   // Hide from responses (for IDs)
)
```

### Attribute Details

#### title (Required)

The label displayed next to the form field.

**Rules:**
- Use Title Case capitalization
- Keep under 25 words
- Avoid technical jargon without explanation
- Include units in parentheses when applicable

```java
// Correct
@ModuleConfigField(title = "Connection Timeout (seconds)")

// Incorrect - camelCase
@ModuleConfigField(title = "connectionTimeoutSeconds")

// Incorrect - no units
@ModuleConfigField(title = "Connection Timeout")
```

#### description (Required)

Help text explaining the field purpose.

**Rules:**
- Keep under 25 words
- Explain what the field does, not just repeat the title
- Mention valid values or expected format
- Use US English spelling

```java
// Correct
@ModuleConfigField(
    title = "Port",
    description = "TCP port number for the device connection (1-65535)"
)

// Incorrect - repeats title
@ModuleConfigField(
    title = "Port",
    description = "The port"
)
```

#### format

Specifies the data format for validation. Maps to JSON Schema `format` property.

**Available FieldType Values:**

| FieldType | JSON Schema format | Use Case |
|-----------|-------------------|----------|
| `IDENTIFIER` | `identifier` | Adapter IDs, unique identifiers |
| `HOSTNAME` | `hostname` | Server hostnames, IP addresses |
| `URI` | `uri` | Full URIs (http://, opc.tcp://) |
| `PASSWORD` | N/A | Sensitive credentials |
| `MQTT_TOPIC` | `mqtt-topic` | MQTT topic strings |
| `MQTT_TOPIC_FILTER` | `mqtt-topic-filter` | MQTT topic filters with wildcards |
| `IPV4` | `ipv4` | IPv4 addresses only |
| `IPV6` | `ipv6` | IPv6 addresses only |
| `EMAIL` | `email` | Email addresses |
| `DATE` | `date` | ISO 8601 date |
| `TIME` | `time` | ISO 8601 time |
| `DATE_TIME` | `date-time` | ISO 8601 datetime |
| `REGEX` | `regex` | Regular expression patterns |
| `JSON_POINTER` | `json-pointer` | JSON pointer syntax |

```java
@ModuleConfigField(
    title = "Host",
    description = "IP address or hostname of the target device",
    format = ModuleConfigField.FieldType.HOSTNAME
)
private @NotNull String host;
```

#### required

Marks the field as mandatory. Use together with `@JsonProperty(required = true)`.

```java
@JsonProperty(value = "host", required = true)
@ModuleConfigField(
    title = "Host",
    description = "IP address or hostname of the target device",
    required = true
)
private @NotNull String host;
```

**Important:** Both annotations must agree. Mismatches cause validation inconsistencies.

#### defaultValue

Specifies the default value as a string. The backend converts it to the appropriate type.

```java
@ModuleConfigField(
    title = "Port",
    description = "TCP port number (default: 502)",
    defaultValue = "502"
)
private @NotNull Integer port;
```

### JSON Schema Output Mapping

| Annotation Attribute | JSON Schema Property |
|---------------------|---------------------|
| `title` | `title` |
| `description` | `description` |
| `required` | Added to `required[]` array |
| `defaultValue` | `default` |
| `format` | `format` |
| `numberMin` | `minimum` |
| `numberMax` | `maximum` |
| `stringMinLength` | `minLength` |
| `stringMaxLength` | `maxLength` |
| `stringPattern` | `pattern` |
| `enumDisplayValues` | `enumNames` |
| `writeOnly` | `writeOnly` |
| `readOnly` | `readOnly` |

---

## 3. Field Types and Constraints

### String Fields

Use `stringMinLength`, `stringMaxLength`, and `stringPattern` for string validation.

```java
@JsonProperty(value = "adapterId", required = true)
@ModuleConfigField(
    title = "Adapter ID",
    description = "Unique identifier for this adapter instance",
    format = ModuleConfigField.FieldType.IDENTIFIER,
    required = true,
    stringMinLength = 1,
    stringMaxLength = 1024,
    stringPattern = "^[a-zA-Z0-9_-]+$"
)
private @NotNull String id;
```

**Generated JSON Schema:**

```json
{
  "id": {
    "type": "string",
    "title": "Adapter ID",
    "description": "Unique identifier for this adapter instance",
    "format": "identifier",
    "minLength": 1,
    "maxLength": 1024,
    "pattern": "^[a-zA-Z0-9_-]+$"
  }
}
```

### Integer and Long Fields

Use `numberMin` and `numberMax` for numeric validation. Never use string constraints on numeric fields.

```java
@JsonProperty(value = "port", required = true)
@ModuleConfigField(
    title = "Port",
    description = "TCP port number for the device connection (1-65535)",
    required = true,
    numberMin = 1,
    numberMax = 65535,
    defaultValue = "502"
)
private @NotNull Integer port;
```

**Generated JSON Schema:**

```json
{
  "port": {
    "type": "integer",
    "title": "Port",
    "description": "TCP port number for the device connection (1-65535)",
    "minimum": 1,
    "maximum": 65535,
    "default": 502
  }
}
```

### Boolean Fields

Boolean fields generate checkbox or toggle widgets automatically.

```java
@JsonProperty("enableTls")
@ModuleConfigField(
    title = "Enable TLS",
    description = "Use encrypted TLS connection to the server",
    defaultValue = "false"
)
private @NotNull Boolean enableTls;
```

**Generated JSON Schema:**

```json
{
  "enableTls": {
    "type": "boolean",
    "title": "Enable TLS",
    "description": "Use encrypted TLS connection to the server",
    "default": false
  }
}
```

### Enum Fields

Enum fields require `enumDisplayValues` to provide user-friendly labels.

```java
public enum DatabaseType {
    POSTGRESQL,
    MYSQL,
    MSSQL
}

@JsonProperty(value = "type", required = true)
@ModuleConfigField(
    title = "Database Type",
    description = "Select the target database system",
    required = true,
    enumDisplayValues = {
        "PostgreSQL",
        "MySQL",
        "Microsoft SQL Server"
    }
)
private @NotNull DatabaseType type;
```

**Generated JSON Schema:**

```json
{
  "type": {
    "type": "string",
    "title": "Database Type",
    "description": "Select the target database system",
    "enum": ["POSTGRESQL", "MYSQL", "MSSQL"],
    "enumNames": ["PostgreSQL", "MySQL", "Microsoft SQL Server"]
  }
}
```

**Critical:** The count of `enumDisplayValues` must match the count of enum constants exactly.

### Nested Object Fields

Nested objects use separate configuration classes with their own annotations.

```java
// Main config class
@JsonProperty("tls")
@ModuleConfigField(
    title = "TLS Configuration",
    description = "Transport Layer Security settings for encrypted connections"
)
private @NotNull TlsConfig tls;

// Nested config class
public class TlsConfig {
    @JsonProperty("enabled")
    @ModuleConfigField(
        title = "Enable TLS",
        description = "Activate TLS encryption for this connection",
        defaultValue = "false"
    )
    private @NotNull Boolean enabled;

    @JsonProperty("trustAllCertificates")
    @ModuleConfigField(
        title = "Trust All Certificates",
        description = "Accept any server certificate (insecure, for testing only)",
        defaultValue = "false"
    )
    private @NotNull Boolean trustAllCertificates;
}
```

### Array Fields

Array fields use `arrayMinItems`, `arrayMaxItems`, and `arrayUniqueItems`.

```java
@JsonProperty(value = "subscriptions", required = true)
@ModuleConfigField(
    title = "Subscriptions",
    description = "List of data points to subscribe to",
    required = true,
    arrayMinItems = 1,
    arrayMaxItems = 1000,
    arrayUniqueItems = true
)
private @NotNull List<SubscriptionConfig> subscriptions;
```

---

## 4. Common Mistakes

This section documents real issues found in HiveMQ Edge adapters with corrected examples.

### Mistake 1: String Constraints on Integer Fields

**Problem:** Using `stringMinLength`, `stringMaxLength`, or `stringPattern` on Integer or Long fields.

**Impact:** JSON Schema validators ignore invalid constraints. The frontend accepts invalid values.

**Before (Incorrect):**

```java
@JsonProperty(value = "port", required = true)
@ModuleConfigField(
    title = "Port",
    description = "Server port",
    required = true,
    stringPattern = "^([a-zA-Z_0-9-_])*$",  // Wrong: String constraint
    stringMinLength = 1,                      // Wrong: String constraint
    stringMaxLength = 6,                      // Wrong: String constraint
    defaultValue = "5432"
)
protected @NotNull Integer port;
```

**After (Correct):**

```java
@JsonProperty(value = "port", required = true)
@ModuleConfigField(
    title = "Port",
    description = "Server port (1-65535)",
    required = true,
    numberMin = 1,                            // Correct: Number constraint
    numberMax = 65535,                        // Correct: Number constraint
    defaultValue = "5432"
)
protected @NotNull Integer port;
```

### Mistake 2: CamelCase Titles

**Problem:** Using Java field names directly as titles.

**Impact:** Users see technical names like "connectionTimeoutSeconds" instead of readable labels.

**Before (Incorrect):**

```java
@ModuleConfigField(
    title = "connectionTimeoutSeconds",      // Wrong: camelCase
    description = "The timeout for connection establishment."
)
private @NotNull Integer connectionTimeoutSeconds;
```

**After (Correct):**

```java
@ModuleConfigField(
    title = "Connection Timeout (seconds)",  // Correct: Title Case with units
    description = "Maximum time to establish a connection to the database"
)
private @NotNull Integer connectionTimeoutSeconds;
```

### Mistake 3: Mismatched Required Annotations

**Problem:** `@JsonProperty(required = true)` does not match `@ModuleConfigField(required = true)`.

**Impact:** Frontend validation passes but backend rejects the request, or vice versa.

**Before (Incorrect):**

```java
@JsonProperty(value = "host", required = true)   // Required for Jackson
@ModuleConfigField(
    title = "Host",
    description = "Target hostname"
    // Missing: required = true                   // Not required in schema
)
private @NotNull String host;
```

**After (Correct):**

```java
@JsonProperty(value = "host", required = true)
@ModuleConfigField(
    title = "Host",
    description = "Target hostname",
    required = true                              // Both annotations agree
)
private @NotNull String host;
```

### Mistake 4: Missing Enum Display Values

**Problem:** Enum field lacks `enumDisplayValues` or count does not match enum constants.

**Impact:** Users see raw enum values like "SIGN_AND_ENCRYPT" instead of "Sign and Encrypt".

**Before (Incorrect):**

```java
@ModuleConfigField(
    title = "Security Mode",
    description = "Message security mode"
    // Missing: enumDisplayValues
)
private @NotNull SecurityMode securityMode;
```

**After (Correct):**

```java
@ModuleConfigField(
    title = "Security Mode",
    description = "Message security level for communication",
    enumDisplayValues = {
        "None",
        "Sign Only",
        "Sign and Encrypt"
    }
)
private @NotNull SecurityMode securityMode;
```

### Mistake 5: Getter Returns Wrong Field

**Problem:** Getter method returns a different field than expected.

**Impact:** Backend logic uses incorrect values, causing runtime bugs.

**Before (Incorrect):**

```java
private @NotNull Boolean encrypt;
private @NotNull Boolean trustCertificate;

public @NotNull Boolean getTrustCertificate() {
    return encrypt;                              // Bug: Returns wrong field
}
```

**After (Correct):**

```java
private @NotNull Boolean encrypt;
private @NotNull Boolean trustCertificate;

public @NotNull Boolean getTrustCertificate() {
    return trustCertificate;                     // Correct: Returns expected field
}
```

### Mistake 6: Question Marks in Titles

**Problem:** Titles phrased as questions.

**Impact:** Inconsistent UI and confused users.

**Before (Incorrect):**

```java
@ModuleConfigField(
    title = "Assert JSON Response?",             // Wrong: Question mark
    description = "Parse response body as JSON"
)
private @NotNull Boolean assertJsonResponse;
```

**After (Correct):**

```java
@ModuleConfigField(
    title = "Assert JSON Response",              // Correct: Statement
    description = "Parse the response body as JSON regardless of Content-Type"
)
private @NotNull Boolean assertJsonResponse;
```

### Mistake 7: Missing Conditional Dependencies

**Problem:** Related fields always visible even when logically dependent on a toggle.

**Impact:** Users see irrelevant fields, causing confusion.

**Example:** `trustCertificate` field appears even when `encrypt` is false.

**Solution:** Add JSON Schema `dependencies` in a custom schema extension or UI schema configuration:

```json
{
  "dependencies": {
    "encrypt": {
      "oneOf": [
        {
          "properties": { "encrypt": { "const": false } }
        },
        {
          "properties": {
            "encrypt": { "const": true },
            "trustCertificate": { "type": "boolean" }
          }
        }
      ]
    }
  }
}
```

### Mistake 8: Grammar and Spelling Errors

**Problem:** Inconsistent grammar in descriptions.

**Impact:** Unprofessional appearance and potential confusion.

**Before (Incorrect):**

```java
@ModuleConfigField(
    title = "Polling Interval [ms]",
    description = "Time in millisecond that this endpoint will be polled"  // Wrong: singular
)
```

**After (Correct):**

```java
@ModuleConfigField(
    title = "Polling Interval (ms)",
    description = "Time in milliseconds between polling requests"          // Correct: plural
)
```

### Mistake 9: Informal Language in Descriptions

**Problem:** Using symbols or informal notation in descriptions.

**Impact:** Unprofessional and potentially confusing.

**Before (Incorrect):**

```java
@ModuleConfigField(
    title = "Port",
    description = "Server port (Default --> PostgreSQL: 5432, MySQL: 3306)"  // Wrong: -->
)
```

**After (Correct):**

```java
@ModuleConfigField(
    title = "Port",
    description = "Server port. Defaults: PostgreSQL 5432, MySQL 3306, MS SQL 1433"
)
```

### Mistake 10: Copy-Paste Errors

**Problem:** Descriptions copied from another adapter without updating.

**Impact:** Incorrect documentation confuses users.

**Before (Incorrect):**

```java
// In BACnet adapter
@ModuleConfigField(
    title = "BACnet/IP To MQTT Config",
    description = "The configuration for a data stream from ADS to MQTT"   // Wrong: ADS
)
```

**After (Correct):**

```java
// In BACnet adapter
@ModuleConfigField(
    title = "BACnet/IP To MQTT Config",
    description = "The configuration for a data stream from BACnet/IP to MQTT"
)
```

---

## 5. Best Practices

### Do's

#### Use Appropriate Constraint Types

Match constraint types to field types:

| Field Type | Use These Constraints |
|------------|----------------------|
| `String` | `stringMinLength`, `stringMaxLength`, `stringPattern` |
| `Integer`, `Long` | `numberMin`, `numberMax` |
| `List<T>` | `arrayMinItems`, `arrayMaxItems`, `arrayUniqueItems` |

#### Provide Meaningful Defaults

Include sensible default values for optional fields:

```java
@ModuleConfigField(
    title = "Timeout (ms)",
    description = "Connection timeout in milliseconds",
    numberMin = 1000,
    numberMax = 300000,
    defaultValue = "5000"                                // Sensible default
)
private @NotNull Integer timeoutMillis;
```

#### Use Format Types for Validation

Apply format types for automatic frontend validation:

```java
// Host field with hostname validation
@ModuleConfigField(
    title = "Host",
    description = "Target device hostname or IP address",
    format = ModuleConfigField.FieldType.HOSTNAME
)
private @NotNull String host;

// URI field with URI validation
@ModuleConfigField(
    title = "Server URI",
    description = "OPC UA server endpoint URI",
    format = ModuleConfigField.FieldType.URI
)
private @NotNull String uri;
```

#### Include Units in Titles

Specify units to avoid ambiguity:

```java
@ModuleConfigField(title = "Timeout (seconds)")      // Correct
@ModuleConfigField(title = "Polling Interval (ms)")  // Correct
@ModuleConfigField(title = "Buffer Size (KB)")       // Correct
```

#### Validate Port Fields Consistently

Use standard port range validation:

```java
@ModuleConfigField(
    title = "Port",
    description = "TCP port number (1-65535)",
    required = true,
    numberMin = 1,
    numberMax = 65535,
    defaultValue = "502"
)
private @NotNull Integer port;
```

#### Set writeOnly for ID Fields

Prevent ID exposure in API responses:

```java
@JsonProperty(value = "id", required = true, access = JsonProperty.Access.WRITE_ONLY)
@ModuleConfigField(
    title = "Adapter ID",
    description = "Unique identifier for this adapter instance",
    required = true,
    writeOnly = true
)
private @NotNull String id;
```

#### Document Acronyms and Technical Terms

Expand acronyms on first use:

```java
@ModuleConfigField(
    title = "AMS Net ID",
    description = "Automation Message Specification (AMS) network identifier for HiveMQ Edge"
)
private @NotNull String sourceAmsNetId;

@ModuleConfigField(
    title = "Remote TSAP",
    description = "Transport Service Access Point (TSAP) for the remote PLC connection"
)
private @NotNull String remoteTsap;
```

### Don'ts

#### Do Not Mix Constraint Types

Never use string constraints on numeric fields:

```java
// Wrong
@ModuleConfigField(
    stringMinLength = 1,                             // Do not use on Integer
    stringMaxLength = 6                              // Do not use on Integer
)
private @NotNull Integer port;
```

#### Do Not Use CamelCase Titles

Convert field names to readable titles:

```java
// Wrong
@ModuleConfigField(title = "pollingIntervalMillis")

// Correct
@ModuleConfigField(title = "Polling Interval (ms)")
```

#### Do Not Omit Required Annotations

Include `required = true` in both annotations:

```java
// Wrong - inconsistent
@JsonProperty(value = "host", required = true)
@ModuleConfigField(title = "Host", description = "...")  // Missing required = true

// Correct - consistent
@JsonProperty(value = "host", required = true)
@ModuleConfigField(title = "Host", description = "...", required = true)
```

#### Do Not Hardcode ui:disabled for ID

The frontend handles ID field state dynamically:

```json
// Wrong - in UI schema
{
  "id": {
    "ui:disabled": true    // Do not hardcode
  }
}
```

The frontend sets `ui:disabled` based on create versus edit mode automatically.

#### Do Not Copy Descriptions Without Updating

Review all copied content for adapter-specific accuracy:

```java
// Wrong - copied from another adapter
description = "Data stream from ADS to MQTT"

// Correct - updated for current adapter
description = "Data stream from BACnet/IP to MQTT"
```

#### Do Not Use Informal Language

Maintain professional documentation style:

```java
// Wrong
description = "Default --> PostgreSQL: 5432"

// Correct
description = "Default ports: PostgreSQL 5432, MySQL 3306"
```

#### Do Not Leave Enum Display Values Empty

Always provide user-friendly labels:

```java
// Wrong
enumDisplayValues = {}

// Correct
enumDisplayValues = {"PostgreSQL", "MySQL", "Microsoft SQL Server"}
```

---

## Quick Reference Checklist

Use this checklist before submitting adapter code for review.

### Field Metadata

- [ ] Every field has a `title` attribute
- [ ] Every field has a `description` attribute
- [ ] Titles use Title Case (not camelCase)
- [ ] Descriptions use correct US English grammar
- [ ] No question marks in titles
- [ ] Technical terms include explanations

### Type Constraints

- [ ] Integer/Long fields use `numberMin`/`numberMax`
- [ ] String fields use `stringMinLength`/`stringMaxLength`/`stringPattern`
- [ ] Port fields have `numberMin=1, numberMax=65535`
- [ ] Timeout fields have reasonable bounds

### Required Fields

- [ ] `@JsonProperty(required=true)` matches `@ModuleConfigField(required=true)`
- [ ] All essential fields marked as required
- [ ] Optional fields have sensible defaults

### Enum Fields

- [ ] `enumDisplayValues` count matches enum constant count
- [ ] Display values are user-friendly
- [ ] Values appear in logical order

### Code Quality

- [ ] Getter methods return the correct field
- [ ] `@JsonCreator` annotation on constructor
- [ ] All `@JsonProperty` annotations present
- [ ] No copy-paste errors from other adapters

---

## Related Documentation

- [UI Schema Configuration Guide](./UI_SCHEMA_CONFIGURATION_GUIDE.md) - Visual customization reference
- [Protocol Adapter QA Checklist](./ADAPTER_QA_CHECKLIST.md) - Complete quality assurance checklist
- [RJSF Documentation](https://rjsf-team.github.io/react-jsonschema-form/docs/) - React JSON Schema Form reference
