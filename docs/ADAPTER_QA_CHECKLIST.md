# Protocol Adapter QA Checklist

**Version:** Draft 1.0
**Purpose:** Quality assurance checklist for HiveMQ Edge protocol adapter development
**Audience:** Adapter developers (Java)

---

## How to Use This Checklist

Run through this checklist before submitting your adapter for review. Each item is marked with:

- **[M]** = Manual review required
- **[A]** = Can be automated (future CLI tool)
- **[V]** = Visual inspection in rendered form

---

## 1. JSON Schema Validation

These checks ensure your `@ModuleConfigField` annotations generate correct JSON Schema.

### 1.1 Field Metadata

| # | Check | Type | Common Mistake |
|---|-------|------|----------------|
| 1.1.1 | Every field has `title` attribute | [A] | Missing title shows raw field name to users |
| 1.1.2 | Every field has `description` attribute | [A] | Missing description provides no help text |
| 1.1.3 | Titles use Title Case (not camelCase) | [A] | `"connectionTimeoutSeconds"` → `"Connection Timeout (seconds)"` |
| 1.1.4 | Descriptions are grammatically correct | [M] | `"millisecond"` → `"milliseconds"` |
| 1.1.5 | Descriptions don't end with question marks | [A] | `"Assert JSON Response?"` → `"Assert JSON Response"` |
| 1.1.6 | No technical jargon without explanation | [M] | `"Remote TSAP"` should explain what TSAP means |

### 1.2 Type Constraints

| # | Check | Type | Common Mistake |
|---|-------|------|----------------|
| 1.2.1 | Integer fields use `numberMin`/`numberMax` | [A] | Using `stringMinLength` on Integer field |
| 1.2.2 | String fields use `stringMinLength`/`stringMaxLength` | [A] | Using `numberMin` on String field |
| 1.2.3 | Port fields have `numberMin=1, numberMax=65535` | [A] | Missing or incorrect port range |
| 1.2.4 | Timeout fields have reasonable bounds | [M] | Unbounded timeouts can cause issues |
| 1.2.5 | `stringPattern` regex is valid | [A] | Invalid regex causes runtime errors |

### 1.3 Required Fields

| # | Check | Type | Common Mistake |
|---|-------|------|----------------|
| 1.3.1 | `@JsonProperty(required=true)` matches `@ModuleConfigField(required=true)` | [A] | Mismatch causes validation inconsistency |
| 1.3.2 | All essential fields marked as required | [M] | Missing required on critical fields |
| 1.3.3 | Optional fields have sensible defaults | [M] | No default on optional field |

### 1.4 Enum Fields

| # | Check | Type | Common Mistake |
|---|-------|------|----------------|
| 1.4.1 | `enumDisplayValues` count matches enum values count | [A] | Mismatch causes rendering issues |
| 1.4.2 | Display values are user-friendly | [M] | `"SIGN_AND_ENCRYPT"` → `"Sign and Encrypt"` |
| 1.4.3 | Enum values are in logical order | [M] | Random ordering confuses users |

### 1.5 Format Types

| # | Check | Type | Common Mistake |
|---|-------|------|----------------|
| 1.5.1 | Host/IP fields use `format = FieldType.HOSTNAME` | [A] | Missing format validation |
| 1.5.2 | URI fields use `format = FieldType.URI` | [A] | URI accepted without validation |
| 1.5.3 | ID fields use `format = FieldType.IDENTIFIER` | [A] | ID field without pattern |
| 1.5.4 | MQTT topics use `format = FieldType.MQTT_TOPIC` | [A] | Topic validation missing |

---

## 2. UI Schema Validation

These checks ensure your UI Schema provides good user experience in the Edge frontend.

### 2.1 Structure

| # | Check | Type | Common Mistake |
|---|-------|------|----------------|
| 2.1.1 | UI Schema JSON file exists in `src/main/resources/` | [A] | Missing UI schema file |
| 2.1.2 | `ui:tabs` array groups related fields | [V] | All fields in one long form |
| 2.1.3 | `ui:order` array defines logical field sequence | [A] | Fields in random order |
| 2.1.4 | Tab names are clear and descriptive | [M] | Generic tab names like "Tab 1" |

### 2.2 Widgets

| # | Check | Type | Common Mistake |
|---|-------|------|----------------|
| 2.2.1 | Port fields use `"ui:widget": "updown"` | [A] | Default text input for ports |
| 2.2.2 | Password fields use `"ui:widget": "password"` | [A] | Passwords shown in plain text |
| 2.2.3 | Multi-line content uses `"ui:widget": "textarea"` | [A] | Single-line input for long text |
| 2.2.4 | Bounded numbers use `"ui:widget": "updown"` | [M] | Text input for numeric fields |

### 2.3 Field Behavior

| # | Check | Type | Common Mistake |
|---|-------|------|----------------|
| 2.3.1 | `id` field does NOT have hardcoded `ui:disabled` | [A] | Should be dynamic (create vs edit) |
| 2.3.2 | `writeOnly` fields (like id) have `access = WRITE_ONLY` | [A] | ID visible in responses |
| 2.3.3 | Read-only fields have `"ui:readonly": true` | [A] | Editable when shouldn't be |

### 2.4 Arrays and Objects

| # | Check | Type | Common Mistake |
|---|-------|------|----------------|
| 2.4.1 | Array mappings use `"ui:batchMode": true` | [V] | Poor UX for bulk operations |
| 2.4.2 | Collapsible arrays have `"ui:collapsable": true` | [V] | Long lists not collapsible |
| 2.4.3 | Collapsible items have `"titleKey"` for display | [A] | Items show as "Item 1, Item 2" |

---

## 3. Conditional Fields (Dependencies)

These checks ensure fields show/hide appropriately based on other field values.

### 3.1 Boolean Toggles

| # | Check | Type | Common Mistake |
|---|-------|------|----------------|
| 3.1.1 | TLS/encryption toggles hide related fields when `false` | [V] | Certificate fields shown when TLS disabled |
| 3.1.2 | Feature toggles use JSON Schema `dependencies` | [A] | All sub-fields always visible |

**Example Pattern:**
```json
{
  "dependencies": {
    "enableTls": {
      "oneOf": [
        { "properties": { "enableTls": { "const": false } } },
        {
          "properties": {
            "enableTls": { "const": true },
            "certificate": { "type": "string" }
          },
          "required": ["certificate"]
        }
      ]
    }
  }
}
```

### 3.2 Cross-Field Validation

| # | Check | Type | Common Mistake |
|---|-------|------|----------------|
| 3.2.1 | Range fields validate `min <= max` | [M] | `startIdx > endIdx` accepted |
| 3.2.2 | Related fields have documented dependencies | [M] | Hidden coupling between fields |

---

## 4. Content Quality

These checks ensure user-facing content meets quality standards.

### 4.1 Language and Grammar

| # | Check | Type | Common Mistake |
|---|-------|------|----------------|
| 4.1.1 | All text is US English | [M] | Mixed language or British spelling |
| 4.1.2 | No abbreviations without context | [M] | "AMS" without explaining what it means |
| 4.1.3 | Consistent terminology across adapter | [M] | "host" vs "server" vs "address" |
| 4.1.4 | No copy-paste errors from other adapters | [M] | "from ADS to MQTT" in BACnet adapter |

### 4.2 Descriptions

| # | Check | Type | Common Mistake |
|---|-------|------|----------------|
| 4.2.1 | Descriptions explain WHAT the field does | [M] | Repeating the title as description |
| 4.2.2 | Descriptions mention valid values/ranges | [M] | No hint about expected format |
| 4.2.3 | Default values mentioned when non-obvious | [M] | User doesn't know what happens if empty |

---

## 5. Backend Code Quality

These checks catch common Java implementation issues.

### 5.1 Getter Methods

| # | Check | Type | Common Mistake |
|---|-------|------|----------------|
| 5.1.1 | Getter returns the correct field | [A] | `getTrustCertificate()` returns `encrypt` |
| 5.1.2 | Boolean getters use `is` prefix | [M] | `getEnabled()` vs `isEnabled()` |

### 5.2 Constructor

| # | Check | Type | Common Mistake |
|---|-------|------|----------------|
| 5.2.1 | `@JsonCreator` on constructor | [A] | Jackson can't deserialize |
| 5.2.2 | All `@JsonProperty` annotations present | [A] | Field not mapped from JSON |
| 5.2.3 | Optional fields have null handling | [A] | NPE on missing optional field |

---

## 6. Visual Testing Checklist

These checks require rendering the form in a browser.

### 6.1 Form Rendering

| # | Check | Type | Common Mistake |
|---|-------|------|----------------|
| 6.1.1 | Form renders without JavaScript errors | [A] | Console errors in browser |
| 6.1.2 | All fields from JSON Schema appear | [A] | Field missing from form |
| 6.1.3 | Field order matches `ui:order` | [V] | Fields in wrong sequence |
| 6.1.4 | Tabs group fields correctly | [V] | Related fields on different tabs |

### 6.2 Validation

| # | Check | Type | Common Mistake |
|---|-------|------|----------------|
| 6.2.1 | Required fields show validation error when empty | [A] | Submit succeeds with empty required field |
| 6.2.2 | Pattern validation shows helpful error | [V] | Generic "invalid" message |
| 6.2.3 | Number bounds validated | [A] | Port 99999 accepted |

### 6.3 Accessibility

| # | Check | Type | Common Mistake |
|---|-------|------|----------------|
| 6.3.1 | No critical axe-core violations | [A] | Form fails accessibility audit |
| 6.3.2 | All inputs have labels | [A] | Unlabeled form fields |
| 6.3.3 | Form navigable by keyboard | [V] | Tab order broken |

---

## Quick Reference: Common Issues by Severity

### 🔴 Critical (Blocks Functionality)

- Getter returns wrong field value
- Wrong constraint type (string constraints on integer)
- Missing `@JsonCreator` or `@JsonProperty`
- Tag schema copied from wrong adapter

### 🟠 High (Poor User Experience)

- Missing `enumDisplayValues` for enums
- Missing conditional field dependencies
- Password fields not masked
- Port fields without number widget

### 🟡 Medium (Polish)

- CamelCase titles instead of Title Case
- Grammar errors in descriptions
- Missing or minimal UI Schema tabs
- Inconsistent terminology

### 🟢 Low (Nice to Have)

- Minor grammar issues ("millisecond" vs "milliseconds")
- Question marks in titles
- Informal language ("-->")

---

## Automation Potential

| Category | Automatable | Manual Review | Visual Check |
|----------|-------------|---------------|--------------|
| JSON Schema | 15 items | 6 items | 0 items |
| UI Schema | 10 items | 2 items | 4 items |
| Dependencies | 2 items | 2 items | 2 items |
| Content | 0 items | 8 items | 0 items |
| Backend Code | 4 items | 2 items | 0 items |
| Visual Testing | 5 items | 0 items | 4 items |
| **Total** | **36 items** | **20 items** | **10 items** |

~55% of checks can potentially be automated via CLI tool.

---

## Revision History

| Version | Date | Changes |
|---------|------|---------|
| Draft 1.0 | 2026-01-21 | Initial draft based on task 38658 methodology |
