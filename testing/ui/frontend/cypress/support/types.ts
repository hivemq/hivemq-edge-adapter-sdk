/**
 * Type definitions for Cypress tests
 */

export interface FieldSchema {
  type?: string
  title?: string
  description?: string
  default?: unknown
  minimum?: number
  maximum?: number
  minLength?: number
  maxLength?: number
  pattern?: string
  format?: string
  enum?: string[]
  enumNames?: string[]
}

export interface ConfigSchema {
  type: string
  properties?: Record<string, FieldSchema>
  required?: string[]
}

export interface UiSchema {
  'ui:tabs'?: Array<{
    id: string
    title: string
    properties: string[]
  }>
  'ui:order'?: string[]
  [key: string]: unknown
}

export interface ProtocolAdapterType {
  id: string
  protocol: string
  name: string
  description?: string
  version?: string
  author?: string
  category?: string
  tags?: string[]
  capabilities?: string[]
  configSchema: ConfigSchema
  uiSchema?: UiSchema
}

export interface ApiResponse {
  items: ProtocolAdapterType[]
}
