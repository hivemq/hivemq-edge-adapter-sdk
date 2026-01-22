/**
 * Custom Cypress commands for adapter form testing
 */

import type { ApiResponse, ProtocolAdapterType } from './types'

declare global {
  namespace Cypress {
    interface Chainable {
      /**
       * Visit the form page and wait for adapter data to load
       * @param adapterId - Optional adapter ID to load specific adapter
       */
      visitForm(adapterId?: string): Chainable<ProtocolAdapterType[]>

      /**
       * Get the current adapter data from the intercepted API response
       */
      getAdapters(): Chainable<ProtocolAdapterType[]>

      /**
       * Get the first (or only) adapter
       */
      getFirstAdapter(): Chainable<ProtocolAdapterType>

      /**
       * Check if form has no console errors
       */
      noConsoleErrors(): Chainable<void>

      /**
       * Submit the form and check for validation
       */
      submitForm(): Chainable<JQuery<HTMLElement>>

      /**
       * Get a form field by its label text
       */
      getFieldByLabel(label: string): Chainable<JQuery<HTMLElement>>
    }
  }
}

// Store adapters from API response
let cachedAdapters: ProtocolAdapterType[] = []

Cypress.Commands.add('visitForm', (adapterId?: string) => {
  // Intercept API before visiting
  cy.intercept('GET', '/api/v1/management/protocol-adapters/types').as('getAdapters')

  // Visit the form route
  const url = adapterId ? `/form/${adapterId}` : '/form'
  cy.visit(url)

  // Wait for API and store response
  return cy.wait('@getAdapters').then((interception) => {
    const response = interception.response?.body as ApiResponse
    cachedAdapters = response?.items || []
    return cy.wrap(cachedAdapters)
  })
})

Cypress.Commands.add('getAdapters', () => {
  return cy.wrap(cachedAdapters)
})

Cypress.Commands.add('getFirstAdapter', () => {
  return cy.wrap(cachedAdapters[0])
})

Cypress.Commands.add('noConsoleErrors', () => {
  cy.window().then((win) => {
    // Check if there were any console errors logged
    // Note: This requires setting up console spy in beforeEach
    cy.wrap(win.console).its('error').should('not.have.been.called')
  })
})

Cypress.Commands.add('submitForm', () => {
  return cy.get('button[type="submit"]').click()
})

Cypress.Commands.add('getFieldByLabel', (label: string) => {
  return cy.contains('label', label).parent().find('input, select, textarea')
})

export {}
