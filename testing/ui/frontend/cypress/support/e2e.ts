/**
 * Cypress E2E support file
 * Loaded before every test file
 */

import './commands'
import 'cypress-real-events'

// Prevent Cypress from failing on uncaught exceptions from the app
Cypress.on('uncaught:exception', (err) => {
  // Log the error for debugging but don't fail the test
  console.warn('Uncaught exception:', err.message)
  return false
})

// Track console errors for noConsoleErrors command
beforeEach(() => {
  cy.window().then((win) => {
    cy.spy(win.console, 'error').as('consoleError')
  })
})
