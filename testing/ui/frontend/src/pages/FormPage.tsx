import { useState, useEffect } from 'react'
import { useParams } from 'react-router-dom'
import {
  Container,
  Alert,
  AlertIcon,
  Spinner,
  Center,
} from '@chakra-ui/react'
import { AdapterForm } from '../components/AdapterForm'
import type { ProtocolAdapterType } from '../hooks/useAdapterTypes'

export function FormPage() {
  const { id } = useParams<{ id?: string }>()
  const [adapter, setAdapter] = useState<ProtocolAdapterType | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [formData, setFormData] = useState<Record<string, unknown>>({})

  useEffect(() => {
    fetchAdapter()
  }, [id])

  const fetchAdapter = async () => {
    try {
      setLoading(true)
      setError(null)
      const response = await fetch('/api/v1/management/protocol-adapters/types')
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`)
      }
      const data = await response.json()
      const items: ProtocolAdapterType[] = data.items || []

      if (items.length === 0) {
        setError('No adapter types found')
        return
      }

      // Select by ID or default to first
      const selected = id
        ? items.find(a => a.id === id)
        : items[0]

      if (!selected) {
        setError(`Adapter "${id}" not found`)
        return
      }

      setAdapter(selected)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load adapter')
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <Center h="100vh">
        <Spinner size="lg" />
      </Center>
    )
  }

  if (error) {
    return (
      <Container maxW="container.lg" py={8}>
        <Alert status="error">
          <AlertIcon />
          {error}
        </Alert>
      </Container>
    )
  }

  if (!adapter) {
    return null
  }

  return (
    <Container maxW="container.lg" py={6}>
      <AdapterForm
        schema={adapter.configSchema}
        uiSchema={adapter.uiSchema}
        formData={formData}
        onChange={setFormData}
      />
    </Container>
  )
}
