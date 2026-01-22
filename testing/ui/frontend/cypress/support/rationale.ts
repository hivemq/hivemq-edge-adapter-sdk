/**
 * Test Rationale Map
 *
 * Maps test IDs to their rationale and suggested fixes.
 * Used by the CI report generator to provide developer-friendly feedback.
 */

export interface TestRationale {
  id: string
  title: string
  rationale: string
  suggestedFix: string
  severity: 'critical' | 'high' | 'medium' | 'low'
  checklistRef: string
}

export const testRationales: Record<string, TestRationale> = {
  // Section 1: JSON Schema Validation
  '[1.1.1]': {
    id: '1.1.1',
    title: 'Every field has title attribute',
    rationale: 'Field titles are displayed as labels in the UI. Missing titles show raw field names (e.g., "connectionTimeoutSeconds") which is confusing for users.',
    suggestedFix: 'Add `title = "Human Readable Name"` to the @ModuleConfigField annotation for each field.',
    severity: 'high',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#1.1.1'
  },
  '[1.1.2]': {
    id: '1.1.2',
    title: 'Every field has description attribute',
    rationale: 'Descriptions provide help text below form fields. Missing descriptions leave users guessing about expected values.',
    suggestedFix: 'Add `description = "Explain what this field does"` to the @ModuleConfigField annotation.',
    severity: 'high',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#1.1.2'
  },
  '[1.1.3]': {
    id: '1.1.3',
    title: 'Titles use Title Case',
    rationale: 'camelCase titles look like programmer variable names. Title Case is more professional and user-friendly.',
    suggestedFix: 'Change title from "connectionTimeout" to "Connection Timeout".',
    severity: 'medium',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#1.1.3'
  },
  '[1.1.5]': {
    id: '1.1.5',
    title: 'Descriptions do not end with question marks',
    rationale: 'Descriptions should be statements, not questions. Questions in descriptions are confusing.',
    suggestedFix: 'Change "Enable TLS?" to "Enable TLS encryption for secure connections".',
    severity: 'low',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#1.1.5'
  },
  '[1.2.1]': {
    id: '1.2.1',
    title: 'Integer fields use number constraints',
    rationale: 'Using string constraints (minLength/maxLength) on integer fields causes validation to fail silently or produce confusing errors.',
    suggestedFix: 'Use `numberMin` and `numberMax` instead of `stringMinLength` and `stringMaxLength` for integer fields.',
    severity: 'critical',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#1.2.1'
  },
  '[1.2.2]': {
    id: '1.2.2',
    title: 'String fields use string constraints',
    rationale: 'Using number constraints (minimum/maximum) on string fields is semantically incorrect and may cause validation issues.',
    suggestedFix: 'Use `stringMinLength` and `stringMaxLength` instead of `numberMin` and `numberMax` for string fields.',
    severity: 'critical',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#1.2.2'
  },
  '[1.2.3]': {
    id: '1.2.3',
    title: 'Port fields have valid port range',
    rationale: 'Ports must be between 1 and 65535. Missing or incorrect bounds allow invalid configurations.',
    suggestedFix: 'Add `numberMin = 1` and `numberMax = 65535` to port field annotations.',
    severity: 'high',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#1.2.3'
  },
  '[1.2.5]': {
    id: '1.2.5',
    title: 'String pattern (regex) is valid',
    rationale: 'Invalid regex patterns cause runtime errors and prevent form validation from working.',
    suggestedFix: 'Test your regex pattern in a regex tester before adding to annotation.',
    severity: 'critical',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#1.2.5'
  },
  '[1.3.1]': {
    id: '1.3.1',
    title: 'Required array contains existing properties',
    rationale: 'Referencing non-existent fields in required array causes schema validation errors.',
    suggestedFix: 'Ensure all field names in @JsonProperty(required=true) match actual field names.',
    severity: 'critical',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#1.3.1'
  },
  '[1.4.1]': {
    id: '1.4.1',
    title: 'enumDisplayValues count matches enum values',
    rationale: 'Mismatched counts cause rendering issues where some options show raw enum values.',
    suggestedFix: 'Ensure enumDisplayValues array has exactly the same number of entries as the enum.',
    severity: 'high',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#1.4.1'
  },
  '[1.5.3]': {
    id: '1.5.3',
    title: 'ID fields use identifier format',
    rationale: 'The identifier format validates that IDs only contain allowed characters (alphanumeric, dash, underscore).',
    suggestedFix: 'Add `format = FieldType.IDENTIFIER` to the id field annotation.',
    severity: 'medium',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#1.5.3'
  },

  // Section 2: UI Schema Validation
  '[2.1.1]': {
    id: '2.1.1',
    title: 'UI Schema exists',
    rationale: 'Without UI Schema, forms use default layout which may not group related fields logically.',
    suggestedFix: 'Create a UI Schema JSON file and implement getUiSchema() in your ProtocolAdapterInformation.',
    severity: 'medium',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#2.1.1'
  },
  '[2.1.2]': {
    id: '2.1.2',
    title: 'ui:tabs groups related fields',
    rationale: 'Tabs organize complex forms into logical sections, improving user experience.',
    suggestedFix: 'Add ui:tabs array to group related fields (e.g., Connection, Authentication, Advanced).',
    severity: 'medium',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#2.1.2'
  },
  '[2.1.3]': {
    id: '2.1.3',
    title: 'ui:order defines field sequence',
    rationale: 'Without ui:order, fields appear in schema order which may not be logical for users.',
    suggestedFix: 'Add ui:order array with fields in logical sequence. Put "id" first, use "*" for remaining.',
    severity: 'medium',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#2.1.3'
  },
  '[2.1.4]': {
    id: '2.1.4',
    title: 'Tab names are descriptive',
    rationale: 'Generic tab names like "Tab 1" do not help users understand the form structure.',
    suggestedFix: 'Use descriptive names like "Connection Settings", "Authentication", "Publishing".',
    severity: 'low',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#2.1.4'
  },
  '[2.2.1]': {
    id: '2.2.1',
    title: 'Port fields use updown widget',
    rationale: 'The updown widget provides increment/decrement buttons for numeric input, improving UX for port numbers.',
    suggestedFix: 'Add `"port": { "ui:widget": "updown" }` to your UI Schema.',
    severity: 'low',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#2.2.1'
  },
  '[2.2.2]': {
    id: '2.2.2',
    title: 'Password fields use password widget',
    rationale: 'Password fields must mask input to prevent shoulder surfing.',
    suggestedFix: 'Add `"password": { "ui:widget": "password" }` to your UI Schema.',
    severity: 'critical',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#2.2.2'
  },
  '[2.3.1]': {
    id: '2.3.1',
    title: 'id field not hardcoded disabled',
    rationale: 'The id field should be editable in create mode but disabled in edit mode. Hardcoding disabled breaks create.',
    suggestedFix: 'Remove `"ui:disabled": true` from id field. The frontend handles this dynamically.',
    severity: 'high',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#2.3.1'
  },

  // Section 6: Visual Testing
  '[6.1.1]': {
    id: '6.1.1',
    title: 'Form renders without JavaScript errors',
    rationale: 'JavaScript errors prevent the form from rendering correctly or at all.',
    suggestedFix: 'Check browser console for errors. Common causes: invalid JSON Schema, missing required fields.',
    severity: 'critical',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#6.1.1'
  },
  '[6.1.2]': {
    id: '6.1.2',
    title: 'All fields appear in form',
    rationale: 'Missing fields prevent users from configuring required options.',
    suggestedFix: 'Verify all @ModuleConfigField annotations have @JsonProperty. Check tab configuration includes all fields.',
    severity: 'critical',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#6.1.2'
  },
  '[6.2.1]': {
    id: '6.2.1',
    title: 'Required fields show validation errors',
    rationale: 'Users need feedback when required fields are empty to know what to fill in.',
    suggestedFix: 'Ensure fields have `required = true` in @ModuleConfigField and matching @JsonProperty(required = true).',
    severity: 'high',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#6.2.1'
  },
  '[6.3.2]': {
    id: '6.3.2',
    title: 'All inputs have labels',
    rationale: 'Screen readers need labels to announce form fields. Missing labels fail accessibility requirements.',
    suggestedFix: 'Ensure all fields have title attribute in @ModuleConfigField.',
    severity: 'high',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#6.3.2'
  },
  '[6.3.3]': {
    id: '6.3.3',
    title: 'Form is keyboard navigable',
    rationale: 'Users who cannot use a mouse need to navigate forms with keyboard.',
    suggestedFix: 'This is usually handled by the framework. Check for custom widgets that may break tab order.',
    severity: 'medium',
    checklistRef: 'ADAPTER_QA_CHECKLIST.md#6.3.3'
  }
}

/**
 * Get rationale for a test by its title
 */
export function getRationaleForTest(testTitle: string): TestRationale | undefined {
  // Extract test ID from title like "[1.1.1] Every field has title"
  const match = testTitle.match(/\[(\d+\.\d+\.\d+)\]/)
  if (match) {
    return testRationales[`[${match[1]}]`]
  }
  return undefined
}
