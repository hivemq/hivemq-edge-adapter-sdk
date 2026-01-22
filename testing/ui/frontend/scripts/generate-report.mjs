#!/usr/bin/env node
/**
 * QA Report Generator
 *
 * Parses Cypress/mochawesome test results and generates a developer-friendly report
 * with rationale and suggested fixes for failures.
 *
 * Usage:
 *   node scripts/generate-report.mjs [results.json]
 */

import * as fs from 'fs'

// Test rationale map - maps test IDs to their rationale and suggested fixes
const testRationales = {
  // Section 1: JSON Schema Validation
  '[1.1.1]': {
    id: '1.1.1',
    title: 'Every field has title attribute',
    rationale: 'Field titles are displayed as labels in the UI. Missing titles show raw field names which confuses users.',
    suggestedFix: 'Add `title = "Human Readable Name"` to the @ModuleConfigField annotation.',
    severity: 'high'
  },
  '[1.1.2]': {
    id: '1.1.2',
    title: 'Every field has description attribute',
    rationale: 'Descriptions provide help text below form fields. Missing descriptions leave users guessing.',
    suggestedFix: 'Add `description = "Explain what this field does"` to @ModuleConfigField.',
    severity: 'high'
  },
  '[1.1.3]': {
    id: '1.1.3',
    title: 'Titles use Title Case',
    rationale: 'camelCase titles look like programmer variable names. Title Case is more professional.',
    suggestedFix: 'Change title from "connectionTimeout" to "Connection Timeout".',
    severity: 'medium'
  },
  '[1.1.5]': {
    id: '1.1.5',
    title: 'Descriptions do not end with question marks',
    rationale: 'Descriptions should be statements, not questions.',
    suggestedFix: 'Change "Enable TLS?" to "Enable TLS encryption for secure connections".',
    severity: 'low'
  },
  '[1.2.1]': {
    id: '1.2.1',
    title: 'Integer fields use number constraints',
    rationale: 'Using string constraints on integer fields causes validation to fail silently.',
    suggestedFix: 'Use `numberMin` and `numberMax` instead of string constraints for integers.',
    severity: 'critical'
  },
  '[1.2.2]': {
    id: '1.2.2',
    title: 'String fields use string constraints',
    rationale: 'Using number constraints on string fields is semantically incorrect.',
    suggestedFix: 'Use `stringMinLength` and `stringMaxLength` for string fields.',
    severity: 'critical'
  },
  '[1.2.3]': {
    id: '1.2.3',
    title: 'Port fields have valid port range',
    rationale: 'Ports must be between 1 and 65535. Missing bounds allow invalid configurations.',
    suggestedFix: 'Add `numberMin = 1` and `numberMax = 65535` to port field annotations.',
    severity: 'high'
  },
  '[1.2.5]': {
    id: '1.2.5',
    title: 'String pattern (regex) is valid',
    rationale: 'Invalid regex patterns cause runtime errors and prevent form validation.',
    suggestedFix: 'Test your regex pattern before adding to annotation.',
    severity: 'critical'
  },
  '[1.3.1]': {
    id: '1.3.1',
    title: 'Required array contains existing properties',
    rationale: 'Referencing non-existent fields in required array causes schema validation errors.',
    suggestedFix: 'Ensure all field names in required match actual field names.',
    severity: 'critical'
  },
  '[1.4.1]': {
    id: '1.4.1',
    title: 'enumDisplayValues count matches enum values',
    rationale: 'Mismatched counts cause rendering issues with some options showing raw enum values.',
    suggestedFix: 'Ensure enumDisplayValues array has exactly the same count as the enum.',
    severity: 'high'
  },
  '[1.5.3]': {
    id: '1.5.3',
    title: 'ID fields use identifier format',
    rationale: 'The identifier format validates IDs only contain allowed characters.',
    suggestedFix: 'Add `format = FieldType.IDENTIFIER` to the id field annotation.',
    severity: 'medium'
  },
  // Section 2: UI Schema
  '[2.1.1]': {
    id: '2.1.1',
    title: 'UI Schema exists',
    rationale: 'Without UI Schema, forms use default layout which may not be logical.',
    suggestedFix: 'Create a UI Schema JSON file and implement getUiSchema().',
    severity: 'medium'
  },
  '[2.1.2]': {
    id: '2.1.2',
    title: 'ui:tabs groups related fields',
    rationale: 'Tabs organize complex forms into logical sections, improving UX.',
    suggestedFix: 'Add ui:tabs array to group related fields.',
    severity: 'medium'
  },
  '[2.1.3]': {
    id: '2.1.3',
    title: 'ui:order defines field sequence',
    rationale: 'Without ui:order, fields appear in schema order which may not be logical.',
    suggestedFix: 'Add ui:order array with fields in logical sequence.',
    severity: 'medium'
  },
  '[2.1.4]': {
    id: '2.1.4',
    title: 'Tab names are descriptive',
    rationale: 'Generic tab names like "Tab 1" do not help users understand the form structure.',
    suggestedFix: 'Use descriptive names like "Connection Settings", "Authentication".',
    severity: 'low'
  },
  '[2.2.1]': {
    id: '2.2.1',
    title: 'Port fields use updown widget',
    rationale: 'The updown widget provides increment/decrement buttons for numeric input.',
    suggestedFix: 'Add `"port": { "ui:widget": "updown" }` to your UI Schema.',
    severity: 'low'
  },
  '[2.2.2]': {
    id: '2.2.2',
    title: 'Password fields use password widget',
    rationale: 'Password fields must mask input to prevent shoulder surfing.',
    suggestedFix: 'Add `"password": { "ui:widget": "password" }` to your UI Schema.',
    severity: 'critical'
  },
  '[2.3.1]': {
    id: '2.3.1',
    title: 'id field not hardcoded disabled',
    rationale: 'The id field should be editable in create mode but disabled in edit mode.',
    suggestedFix: 'Remove `"ui:disabled": true` from id field.',
    severity: 'high'
  },
  // Section 6: Visual Testing
  '[6.1.1]': {
    id: '6.1.1',
    title: 'Form renders without JavaScript errors',
    rationale: 'JavaScript errors prevent the form from rendering correctly.',
    suggestedFix: 'Check browser console for errors. Common causes: invalid JSON Schema.',
    severity: 'critical'
  },
  '[6.1.2]': {
    id: '6.1.2',
    title: 'All fields appear in form',
    rationale: 'Missing fields prevent users from configuring required options.',
    suggestedFix: 'Verify all @ModuleConfigField annotations have @JsonProperty.',
    severity: 'critical'
  },
  '[6.2.1]': {
    id: '6.2.1',
    title: 'Required fields show validation errors',
    rationale: 'Users need feedback when required fields are empty.',
    suggestedFix: 'Ensure fields have `required = true` in @ModuleConfigField.',
    severity: 'high'
  },
  '[6.3.2]': {
    id: '6.3.2',
    title: 'All inputs have labels',
    rationale: 'Screen readers need labels to announce form fields.',
    suggestedFix: 'Ensure all fields have title attribute in @ModuleConfigField.',
    severity: 'high'
  },
  '[6.3.3]': {
    id: '6.3.3',
    title: 'Form is keyboard navigable',
    rationale: 'Users who cannot use a mouse need to navigate forms with keyboard.',
    suggestedFix: 'Check for custom widgets that may break tab order.',
    severity: 'medium'
  }
}

function extractTestId(title) {
  const match = title.match(/\[(\d+\.\d+\.\d+)\]/)
  return match ? match[1] : null
}

function collectTests(suite, tests) {
  tests.push(...suite.tests)
  for (const child of suite.suites || []) {
    collectTests(child, tests)
  }
}

function generateReport(results) {
  const report = []
  const summary = {
    total: results.stats.tests,
    passed: results.stats.passes,
    failed: results.stats.failures,
    critical: 0,
    high: 0,
    medium: 0,
    low: 0
  }

  // Collect all tests from nested suites
  const allTests = []
  for (const result of results.results) {
    for (const suite of result.suites || []) {
      collectTests(suite, allTests)
    }
  }

  // Process each test result
  for (const test of allTests) {
    const fullTitle = test.fullTitle
    const testId = extractTestId(fullTitle)
    const rationale = testId ? testRationales[`[${testId}]`] : undefined

    const item = {
      testId: testId || 'N/A',
      title: fullTitle,
      status: test.pass ? 'passed' : 'failed',
      severity: rationale?.severity,
      rationale: rationale?.rationale,
      suggestedFix: rationale?.suggestedFix
    }

    if (test.err) {
      item.errorMessage = test.err.message
    }

    report.push(item)

    // Update severity counts for failures
    if (test.fail && rationale?.severity) {
      summary[rationale.severity]++
    }
  }

  // Output report
  console.log('\n' + '='.repeat(80))
  console.log('ADAPTER QA REPORT')
  console.log('='.repeat(80))
  console.log(`\nGenerated: ${new Date().toISOString()}`)
  console.log(`\n## Summary\n`)
  console.log(`Total Tests: ${summary.total}`)
  console.log(`Passed: ${summary.passed}`)
  console.log(`Failed: ${summary.failed}`)

  if (summary.failed > 0) {
    console.log(`\nFailures by Severity:`)
    if (summary.critical > 0) console.log(`  🔴 Critical: ${summary.critical}`)
    if (summary.high > 0) console.log(`  🟠 High: ${summary.high}`)
    if (summary.medium > 0) console.log(`  🟡 Medium: ${summary.medium}`)
    if (summary.low > 0) console.log(`  🟢 Low: ${summary.low}`)
  }

  // Group by status
  const failed = report.filter(r => r.status === 'failed')
  const passed = report.filter(r => r.status === 'passed')

  if (failed.length > 0) {
    console.log(`\n## Failed Checks (${failed.length})\n`)

    // Sort by severity
    const severityOrder = { critical: 0, high: 1, medium: 2, low: 3 }
    failed.sort((a, b) => {
      const aOrder = a.severity ? severityOrder[a.severity] : 4
      const bOrder = b.severity ? severityOrder[b.severity] : 4
      return aOrder - bOrder
    })

    for (const item of failed) {
      const severityIcon = {
        critical: '🔴',
        high: '🟠',
        medium: '🟡',
        low: '🟢'
      }[item.severity || 'medium']

      console.log(`### ${severityIcon} [${item.testId}] ${item.title.split(' > ').pop()}`)
      console.log()
      if (item.rationale) {
        console.log(`**Why this matters:** ${item.rationale}`)
        console.log()
      }
      if (item.suggestedFix) {
        console.log(`**How to fix:** ${item.suggestedFix}`)
        console.log()
      }
      if (item.errorMessage) {
        console.log(`**Error:** \`${item.errorMessage.split('\n')[0]}\``)
        console.log()
      }
      console.log('---')
    }
  }

  if (passed.length > 0 && failed.length === 0) {
    console.log(`\n## All Checks Passed! ✅\n`)
    console.log(`${passed.length} checks completed successfully.`)
  } else if (passed.length > 0) {
    console.log(`\n## Passed Checks (${passed.length})\n`)
    for (const item of passed) {
      console.log(`✅ [${item.testId}] ${item.title.split(' > ').pop()}`)
    }
  }

  console.log('\n' + '='.repeat(80))
  console.log('END OF REPORT')
  console.log('='.repeat(80) + '\n')

  // Write JSON report
  const jsonReport = {
    generated: new Date().toISOString(),
    summary,
    failed,
    passed
  }

  fs.writeFileSync('qa-report.json', JSON.stringify(jsonReport, null, 2))
  console.log('JSON report written to: qa-report.json')
}

// Main
const resultsFile = process.argv[2] || 'cypress/results/combined.json'

if (!fs.existsSync(resultsFile)) {
  console.error(`Results file not found: ${resultsFile}`)
  console.error('Run: npm run cypress:ci && npm run qa:merge')
  process.exit(1)
}

const results = JSON.parse(fs.readFileSync(resultsFile, 'utf-8'))
generateReport(results)
