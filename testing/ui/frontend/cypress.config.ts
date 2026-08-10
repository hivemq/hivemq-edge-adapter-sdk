import { defineConfig } from 'cypress'

export default defineConfig({
  // Cypress 15.10 deprecated the browser-readable Cypress.env() API and warns on every run while the
  // compatibility shim is on. Nothing here reads it, so close the migration out now rather than
  // inheriting a hard failure when the next major drops the shim.
  allowCypressEnv: false,
  e2e: {
    baseUrl: 'http://localhost:8080',
    supportFile: 'cypress/support/e2e.ts',
    specPattern: 'cypress/e2e/**/*.cy.{js,jsx,ts,tsx}',
    viewportWidth: 1280,
    viewportHeight: 720,
    reporter: 'mochawesome',
    reporterOptions: {
      reportDir: 'cypress/results',
      overwrite: false,
      html: false,
      json: true,
    },
  },
})
