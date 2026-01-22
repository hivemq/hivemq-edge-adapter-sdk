import { useState, useEffect } from 'react'
import type { RJSFSchema, UiSchema } from '@rjsf/utils'

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
  configSchema: RJSFSchema
  uiSchema?: UiSchema
}

interface ApiResponse {
  items: ProtocolAdapterType[]
}

export function useAdapterTypes() {
  const [adapterTypes, setAdapterTypes] = useState<ProtocolAdapterType[]>([])
  const [selectedAdapter, setSelectedAdapter] = useState<ProtocolAdapterType | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetchAdapterTypes()
  }, [])

  const fetchAdapterTypes = async () => {
    try {
      setLoading(true)
      setError(null)
      const response = await fetch('/api/v1/management/protocol-adapters/types')
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`)
      }
      const data: ApiResponse = await response.json()
      setAdapterTypes(data.items || [])

      // Auto-select first adapter if available
      if (data.items && data.items.length > 0) {
        setSelectedAdapter(data.items[0])
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load adapter types')
    } finally {
      setLoading(false)
    }
  }

  const selectAdapter = (adapterId: string) => {
    const adapter = adapterTypes.find(a => a.id === adapterId)
    setSelectedAdapter(adapter || null)
  }

  return {
    adapterTypes,
    selectedAdapter,
    loading,
    error,
    selectAdapter,
  }
}
