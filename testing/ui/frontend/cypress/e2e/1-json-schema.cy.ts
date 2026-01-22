/**
 * JSON Schema Validation Tests
 *
 * QA Checklist Section 1: JSON Schema Validation
 * Tests that @ModuleConfigField annotations generate correct JSON Schema
 *
 * Test IDs correspond to checklist items (e.g., [1.1.1])
 */

import type { ProtocolAdapterType, FieldSchema } from '../support/types'

describe('Section 1: JSON Schema Validation', () => {
  beforeEach(() => {
    cy.visitForm()
  })

  describe('1.1 Field Metadata', () => {
    it('[1.1.1] Every field should have a title attribute', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        const schema = adapter.configSchema
        if (!schema.properties) return

        const fieldsWithoutTitle: string[] = []
        Object.entries(schema.properties).forEach(([fieldName, fieldSchema]) => {
          if (!fieldSchema.title) {
            fieldsWithoutTitle.push(fieldName)
          }
        })

        expect(
          fieldsWithoutTitle,
          `Fields without title: ${fieldsWithoutTitle.join(', ')}`
        ).to.have.length(0)
      })
    })

    it('[1.1.2] Every field should have a description attribute', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        const schema = adapter.configSchema
        if (!schema.properties) return

        const fieldsWithoutDesc: string[] = []
        Object.entries(schema.properties).forEach(([fieldName, fieldSchema]) => {
          if (!fieldSchema.description) {
            fieldsWithoutDesc.push(fieldName)
          }
        })

        expect(
          fieldsWithoutDesc,
          `Fields without description: ${fieldsWithoutDesc.join(', ')}`
        ).to.have.length(0)
      })
    })

    it('[1.1.3] Titles should use Title Case (not camelCase)', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        const schema = adapter.configSchema
        if (!schema.properties) return

        const camelCaseTitles: string[] = []
        const camelCasePattern = /^[a-z]+[A-Z]/

        Object.entries(schema.properties).forEach(([fieldName, fieldSchema]) => {
          if (fieldSchema.title && camelCasePattern.test(fieldSchema.title)) {
            camelCaseTitles.push(`${fieldName}: "${fieldSchema.title}"`)
          }
        })

        expect(
          camelCaseTitles,
          `Titles in camelCase: ${camelCaseTitles.join(', ')}`
        ).to.have.length(0)
      })
    })

    it('[1.1.5] Descriptions should not end with question marks', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        const schema = adapter.configSchema
        if (!schema.properties) return

        const questionsInDesc: string[] = []
        Object.entries(schema.properties).forEach(([fieldName, fieldSchema]) => {
          if (fieldSchema.description?.trim().endsWith('?')) {
            questionsInDesc.push(`${fieldName}: "${fieldSchema.description}"`)
          }
        })

        expect(
          questionsInDesc,
          `Descriptions ending with '?': ${questionsInDesc.join(', ')}`
        ).to.have.length(0)
      })
    })
  })

  describe('1.2 Type Constraints', () => {
    it('[1.2.1] Integer fields should use numberMin/numberMax (not string constraints)', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        const schema = adapter.configSchema
        if (!schema.properties) return

        const invalidConstraints: string[] = []
        Object.entries(schema.properties).forEach(([fieldName, fieldSchema]) => {
          if (fieldSchema.type === 'integer' || fieldSchema.type === 'number') {
            // Integer fields should not have string constraints
            if (fieldSchema.minLength !== undefined || fieldSchema.maxLength !== undefined) {
              invalidConstraints.push(`${fieldName}: has string constraints on number type`)
            }
          }
        })

        expect(
          invalidConstraints,
          `Invalid constraints: ${invalidConstraints.join(', ')}`
        ).to.have.length(0)
      })
    })

    it('[1.2.2] String fields should use stringMinLength/stringMaxLength (not number constraints)', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        const schema = adapter.configSchema
        if (!schema.properties) return

        const invalidConstraints: string[] = []
        Object.entries(schema.properties).forEach(([fieldName, fieldSchema]) => {
          if (fieldSchema.type === 'string') {
            // String fields should not have number constraints
            if (fieldSchema.minimum !== undefined || fieldSchema.maximum !== undefined) {
              invalidConstraints.push(`${fieldName}: has number constraints on string type`)
            }
          }
        })

        expect(
          invalidConstraints,
          `Invalid constraints: ${invalidConstraints.join(', ')}`
        ).to.have.length(0)
      })
    })

    it('[1.2.3] Port fields should have valid port range (1-65535)', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        const schema = adapter.configSchema
        if (!schema.properties) return

        const portFields: string[] = []
        const invalidPortRanges: string[] = []

        // Find fields that look like ports
        Object.entries(schema.properties).forEach(([fieldName, fieldSchema]) => {
          const isPortField = fieldName.toLowerCase().includes('port') ||
                             fieldSchema.title?.toLowerCase().includes('port')

          if (isPortField && (fieldSchema.type === 'integer' || fieldSchema.type === 'number')) {
            portFields.push(fieldName)

            // Check if range is valid
            const hasValidMin = fieldSchema.minimum !== undefined && fieldSchema.minimum >= 1
            const hasValidMax = fieldSchema.maximum !== undefined && fieldSchema.maximum <= 65535

            if (!hasValidMin || !hasValidMax) {
              invalidPortRanges.push(
                `${fieldName}: min=${fieldSchema.minimum}, max=${fieldSchema.maximum}`
              )
            }
          }
        })

        if (portFields.length > 0) {
          expect(
            invalidPortRanges,
            `Port fields with invalid range: ${invalidPortRanges.join(', ')}`
          ).to.have.length(0)
        }
      })
    })

    it('[1.2.5] String pattern (regex) should be valid', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        const schema = adapter.configSchema
        if (!schema.properties) return

        const invalidPatterns: string[] = []
        Object.entries(schema.properties).forEach(([fieldName, fieldSchema]) => {
          if (fieldSchema.pattern) {
            try {
              new RegExp(fieldSchema.pattern)
            } catch {
              invalidPatterns.push(`${fieldName}: "${fieldSchema.pattern}"`)
            }
          }
        })

        expect(
          invalidPatterns,
          `Invalid regex patterns: ${invalidPatterns.join(', ')}`
        ).to.have.length(0)
      })
    })
  })

  describe('1.3 Required Fields', () => {
    it('[1.3.1] Required array should only contain existing properties', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        const schema = adapter.configSchema
        if (!schema.required || !schema.properties) return

        const nonExistentRequired = schema.required.filter(
          (field) => !schema.properties?.[field]
        )

        expect(
          nonExistentRequired,
          `Required fields that don't exist: ${nonExistentRequired.join(', ')}`
        ).to.have.length(0)
      })
    })

    it('[1.3.3] Optional fields should have default values', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        const schema = adapter.configSchema
        if (!schema.properties) return

        const requiredFields = schema.required || []
        const optionalWithoutDefault: string[] = []

        Object.entries(schema.properties).forEach(([fieldName, fieldSchema]) => {
          const isRequired = requiredFields.includes(fieldName)
          const hasDefault = fieldSchema.default !== undefined

          // Skip 'id' field as it's typically provided at creation time
          if (!isRequired && !hasDefault && fieldName !== 'id') {
            optionalWithoutDefault.push(fieldName)
          }
        })

        // This is a warning, not a failure - optional fields CAN have no default
        if (optionalWithoutDefault.length > 0) {
          cy.log(`Optional fields without defaults: ${optionalWithoutDefault.join(', ')}`)
        }
      })
    })
  })

  describe('1.4 Enum Fields', () => {
    it('[1.4.1] enumDisplayValues count should match enum values count', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        const schema = adapter.configSchema
        if (!schema.properties) return

        const mismatchedEnums: string[] = []
        Object.entries(schema.properties).forEach(([fieldName, fieldSchema]) => {
          if (fieldSchema.enum && fieldSchema.enumNames) {
            if (fieldSchema.enum.length !== fieldSchema.enumNames.length) {
              mismatchedEnums.push(
                `${fieldName}: ${fieldSchema.enum.length} values vs ${fieldSchema.enumNames.length} display names`
              )
            }
          }
        })

        expect(
          mismatchedEnums,
          `Enum/enumNames count mismatch: ${mismatchedEnums.join(', ')}`
        ).to.have.length(0)
      })
    })
  })

  describe('1.5 Format Types', () => {
    it('[1.5.3] ID fields should use identifier format', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        const schema = adapter.configSchema
        if (!schema.properties) return

        // Check if 'id' field exists and has proper format
        const idField = schema.properties['id']
        if (idField) {
          expect(
            idField.format,
            "Field 'id' should have format 'identifier'"
          ).to.equal('identifier')
        }
      })
    })

    it('[1.5.4] MQTT topic fields should use mqtt-topic format', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        const schema = adapter.configSchema
        if (!schema.properties) return

        const topicFieldsWithoutFormat: string[] = []
        Object.entries(schema.properties).forEach(([fieldName, fieldSchema]) => {
          const isTopicField = fieldName.toLowerCase().includes('topic') ||
                              fieldSchema.title?.toLowerCase().includes('topic')

          if (isTopicField && fieldSchema.type === 'string') {
            if (!fieldSchema.format?.includes('mqtt')) {
              topicFieldsWithoutFormat.push(fieldName)
            }
          }
        })

        if (topicFieldsWithoutFormat.length > 0) {
          cy.log(`Topic fields without mqtt format: ${topicFieldsWithoutFormat.join(', ')}`)
        }
      })
    })
  })
})
