/**
 * UI Schema Validation Tests
 *
 * QA Checklist Section 2: UI Schema Validation
 * Tests that UI Schema provides good user experience
 *
 * Test IDs correspond to checklist items (e.g., [2.1.1])
 */

import type { ProtocolAdapterType } from '../support/types'

describe('Section 2: UI Schema Validation', () => {
  beforeEach(() => {
    cy.visitForm()
  })

  describe('2.1 Structure', () => {
    it('[2.1.1] UI Schema should exist', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        // UI Schema is optional but recommended
        if (!adapter.uiSchema) {
          cy.log('WARNING: No UI Schema defined - form will use default layout')
        } else {
          expect(adapter.uiSchema).to.be.an('object')
        }
      })
    })

    it('[2.1.2] ui:tabs should group related fields', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        if (!adapter.uiSchema?.['ui:tabs']) {
          cy.log('No ui:tabs defined - single form layout')
          return
        }

        const tabs = adapter.uiSchema['ui:tabs']
        expect(tabs).to.be.an('array')
        expect(tabs.length, 'Should have at least one tab').to.be.greaterThan(0)

        // Each tab should have required properties
        tabs.forEach((tab, index) => {
          expect(tab.id, `Tab ${index} should have id`).to.exist
          expect(tab.title, `Tab ${index} should have title`).to.exist
          expect(tab.properties, `Tab ${index} should have properties array`).to.be.an('array')
        })
      })
    })

    it('[2.1.3] ui:order should define logical field sequence', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        if (!adapter.uiSchema?.['ui:order']) {
          cy.log('No ui:order defined - fields will appear in schema order')
          return
        }

        const order = adapter.uiSchema['ui:order']
        expect(order).to.be.an('array')

        // Check that 'id' comes first (common convention)
        if (order.includes('id')) {
          const idIndex = order.indexOf('id')
          expect(idIndex, "'id' field should be first in ui:order").to.equal(0)
        }

        // Wildcard '*' should be last if present
        if (order.includes('*')) {
          const wildcardIndex = order.indexOf('*')
          expect(
            wildcardIndex,
            "'*' wildcard should be last in ui:order"
          ).to.equal(order.length - 1)
        }
      })
    })

    it('[2.1.4] Tab names should be clear and descriptive', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        if (!adapter.uiSchema?.['ui:tabs']) return

        const genericNames = ['Tab 1', 'Tab 2', 'Tab 3', 'Tab', 'Section', 'Other']
        const badTabNames: string[] = []

        adapter.uiSchema['ui:tabs'].forEach((tab) => {
          if (genericNames.some(generic => tab.title.toLowerCase() === generic.toLowerCase())) {
            badTabNames.push(tab.title)
          }
          // Tab titles should be at least 3 characters
          if (tab.title.length < 3) {
            badTabNames.push(`"${tab.title}" (too short)`)
          }
        })

        expect(
          badTabNames,
          `Generic or unclear tab names: ${badTabNames.join(', ')}`
        ).to.have.length(0)
      })
    })

    it('Tab properties should reference existing schema fields', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        if (!adapter.uiSchema?.['ui:tabs'] || !adapter.configSchema.properties) return

        const schemaFields = Object.keys(adapter.configSchema.properties)
        const unknownFields: string[] = []

        adapter.uiSchema['ui:tabs'].forEach((tab) => {
          tab.properties.forEach((prop) => {
            if (!schemaFields.includes(prop)) {
              unknownFields.push(`${tab.title}/${prop}`)
            }
          })
        })

        expect(
          unknownFields,
          `Tab properties not in schema: ${unknownFields.join(', ')}`
        ).to.have.length(0)
      })
    })
  })

  describe('2.2 Widgets', () => {
    it('[2.2.1] Port fields should use updown widget', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        if (!adapter.uiSchema || !adapter.configSchema.properties) return

        const portFieldsWithoutWidget: string[] = []

        Object.keys(adapter.configSchema.properties).forEach((fieldName) => {
          const isPortField = fieldName.toLowerCase().includes('port')
          const fieldUiSchema = adapter.uiSchema?.[fieldName] as Record<string, unknown> | undefined

          if (isPortField) {
            if (!fieldUiSchema?.['ui:widget'] || fieldUiSchema['ui:widget'] !== 'updown') {
              portFieldsWithoutWidget.push(fieldName)
            }
          }
        })

        if (portFieldsWithoutWidget.length > 0) {
          cy.log(`Port fields without updown widget: ${portFieldsWithoutWidget.join(', ')}`)
        }
      })
    })

    it('[2.2.2] Password fields should use password widget', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        if (!adapter.uiSchema || !adapter.configSchema.properties) return

        const passwordFieldsWithoutWidget: string[] = []
        const passwordPatterns = ['password', 'secret', 'apikey', 'api_key', 'token']

        Object.keys(adapter.configSchema.properties).forEach((fieldName) => {
          const isPasswordField = passwordPatterns.some(
            (p) => fieldName.toLowerCase().includes(p)
          )
          const fieldUiSchema = adapter.uiSchema?.[fieldName] as Record<string, unknown> | undefined

          if (isPasswordField) {
            if (!fieldUiSchema?.['ui:widget'] || fieldUiSchema['ui:widget'] !== 'password') {
              passwordFieldsWithoutWidget.push(fieldName)
            }
          }
        })

        expect(
          passwordFieldsWithoutWidget,
          `Password fields without password widget: ${passwordFieldsWithoutWidget.join(', ')}`
        ).to.have.length(0)
      })
    })
  })

  describe('2.3 Field Behavior', () => {
    it('[2.3.1] id field should NOT have hardcoded ui:disabled', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        if (!adapter.uiSchema) return

        const idFieldUi = adapter.uiSchema['id'] as Record<string, unknown> | undefined

        if (idFieldUi?.['ui:disabled'] === true) {
          throw new Error(
            "Field 'id' has hardcoded ui:disabled. " +
            'This should be dynamic (disabled in edit mode, enabled in create mode).'
          )
        }
      })
    })
  })

  describe('2.4 Arrays and Objects', () => {
    it('[2.4.3] Collapsible array items should have titleKey', () => {
      cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
        if (!adapter.uiSchema || !adapter.configSchema.properties) return

        const arrayFieldsWithoutTitleKey: string[] = []

        Object.entries(adapter.configSchema.properties).forEach(([fieldName, fieldSchema]) => {
          if (fieldSchema.type === 'array') {
            const fieldUi = adapter.uiSchema?.[fieldName] as Record<string, unknown> | undefined

            // If collapsible is enabled, titleKey should be set
            if (fieldUi?.['ui:collapsable'] === true || fieldUi?.['ui:collapsible'] === true) {
              if (!fieldUi?.['titleKey']) {
                arrayFieldsWithoutTitleKey.push(fieldName)
              }
            }
          }
        })

        if (arrayFieldsWithoutTitleKey.length > 0) {
          cy.log(
            `Collapsible arrays without titleKey: ${arrayFieldsWithoutTitleKey.join(', ')}`
          )
        }
      })
    })
  })
})

describe('UI Schema - Form Rendering', () => {
  beforeEach(() => {
    cy.visitForm()
  })

  it('Tabs should render when ui:tabs is configured', () => {
    cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
      if (!adapter.uiSchema?.['ui:tabs']) {
        cy.log('No ui:tabs - skipping tab rendering test')
        return
      }

      // Check for tab list in the form
      cy.get('form [role="tablist"]').should('exist')

      // Verify tab count matches ui:tabs
      const expectedTabs = adapter.uiSchema['ui:tabs'].length
      cy.get('form [role="tab"]').should('have.length', expectedTabs)
    })
  })

  it('Tab titles should match ui:tabs configuration', () => {
    cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
      if (!adapter.uiSchema?.['ui:tabs']) return

      adapter.uiSchema['ui:tabs'].forEach((tab) => {
        cy.get('form [role="tab"]').contains(tab.title).should('exist')
      })
    })
  })

  it('Clicking tabs should switch content', () => {
    cy.getFirstAdapter().then((adapter: ProtocolAdapterType) => {
      if (!adapter.uiSchema?.['ui:tabs'] || adapter.uiSchema['ui:tabs'].length < 2) {
        cy.log('Need at least 2 tabs to test tab switching')
        return
      }

      const secondTab = adapter.uiSchema['ui:tabs'][1]

      // Click second tab
      cy.get('form [role="tab"]').contains(secondTab.title).click()

      // Verify tab panel is displayed
      cy.get('form [role="tabpanel"]').should('be.visible')
    })
  })
})
