import { useState } from 'react'
import {
  Box,
  Container,
  Heading,
  Select,
  Text,
  VStack,
  HStack,
  Badge,
  Alert,
  AlertIcon,
  Spinner,
  Card,
  CardBody,
  CardHeader,
  Tabs,
  TabList,
  TabPanels,
  Tab,
  TabPanel,
  Code,
} from '@chakra-ui/react'
import { Link } from 'react-router-dom'
import { AdapterForm } from '../components/AdapterForm'
import { useAdapterTypes } from '../hooks/useAdapterTypes'

export function MainPage() {
  const { adapterTypes, selectedAdapter, loading, error, selectAdapter } = useAdapterTypes()
  const [formData, setFormData] = useState<Record<string, unknown>>({})

  const handleAdapterChange = (adapterId: string) => {
    selectAdapter(adapterId)
    setFormData({})
  }

  if (loading) {
    return (
      <Container maxW="container.xl" py={8}>
        <VStack spacing={4}>
          <Spinner size="xl" />
          <Text>Loading adapter types...</Text>
        </VStack>
      </Container>
    )
  }

  return (
    <Container maxW="container.xl" py={8}>
      <VStack spacing={6} align="stretch">
        <Box>
          <HStack justify="space-between" align="start">
            <Box>
              <Heading size="lg" mb={2}>HiveMQ Adapter Configuration Tester</Heading>
              <Text color="gray.600">
                Visual testing tool for protocol adapter configuration forms
              </Text>
            </Box>
            <Text fontSize="sm" color="gray.500">
              <Link to="/form" style={{ textDecoration: 'underline' }}>
                Form only view
              </Link>
            </Text>
          </HStack>
        </Box>

        {error && (
          <Alert status="error">
            <AlertIcon />
            {error}
          </Alert>
        )}

        {adapterTypes.length === 0 && !error && (
          <Alert status="warning">
            <AlertIcon />
            No adapter types found. Make sure your adapter JAR is on the classpath.
          </Alert>
        )}

        {adapterTypes.length > 0 && (
          <Box>
            <Text fontWeight="medium" mb={2}>Select Adapter Type:</Text>
            <Select
              value={selectedAdapter?.id || ''}
              onChange={(e) => handleAdapterChange(e.target.value)}
              placeholder="Choose an adapter..."
            >
              {adapterTypes.map(adapter => (
                <option key={adapter.id} value={adapter.id}>
                  {adapter.name} ({adapter.id})
                </option>
              ))}
            </Select>
          </Box>
        )}

        {selectedAdapter && (
          <Card>
            <CardHeader pb={2}>
              <HStack justify="space-between" align="start">
                <Box>
                  <Heading size="md">{selectedAdapter.name}</Heading>
                  {selectedAdapter.description && (
                    <Text color="gray.600" fontSize="sm" mt={1}>
                      {selectedAdapter.description}
                    </Text>
                  )}
                </Box>
                <VStack align="end" spacing={1}>
                  {selectedAdapter.version && (
                    <Badge colorScheme="blue">v{selectedAdapter.version}</Badge>
                  )}
                  {selectedAdapter.category && (
                    <Badge colorScheme="green">{selectedAdapter.category}</Badge>
                  )}
                </VStack>
              </HStack>
              {selectedAdapter.tags && selectedAdapter.tags.length > 0 && (
                <HStack mt={2} spacing={2}>
                  {selectedAdapter.tags.map(tag => (
                    <Badge key={tag} colorScheme="orange" variant="outline" size="sm">
                      {tag}
                    </Badge>
                  ))}
                </HStack>
              )}
              {selectedAdapter.capabilities && selectedAdapter.capabilities.length > 0 && (
                <HStack mt={2} spacing={2}>
                  {selectedAdapter.capabilities.map(cap => (
                    <Badge key={cap} colorScheme="purple" variant="outline">
                      {cap}
                    </Badge>
                  ))}
                </HStack>
              )}
            </CardHeader>
            <CardBody>
              <Tabs>
                <TabList>
                  <Tab>Configuration Form</Tab>
                  <Tab>JSON Schema</Tab>
                  <Tab>UI Schema</Tab>
                  <Tab>Form Data</Tab>
                </TabList>

                <TabPanels>
                  <TabPanel>
                    <AdapterForm
                      schema={selectedAdapter.configSchema}
                      uiSchema={selectedAdapter.uiSchema}
                      formData={formData}
                      onChange={setFormData}
                    />
                  </TabPanel>

                  <TabPanel>
                    <Code
                      display="block"
                      whiteSpace="pre"
                      p={4}
                      borderRadius="md"
                      overflowX="auto"
                      fontSize="sm"
                    >
                      {JSON.stringify(selectedAdapter.configSchema, null, 2)}
                    </Code>
                  </TabPanel>

                  <TabPanel>
                    {selectedAdapter.uiSchema ? (
                      <Code
                        display="block"
                        whiteSpace="pre"
                        p={4}
                        borderRadius="md"
                        overflowX="auto"
                        fontSize="sm"
                      >
                        {JSON.stringify(selectedAdapter.uiSchema, null, 2)}
                      </Code>
                    ) : (
                      <Text color="gray.500">No UI Schema defined</Text>
                    )}
                  </TabPanel>

                  <TabPanel>
                    <Code
                      display="block"
                      whiteSpace="pre"
                      p={4}
                      borderRadius="md"
                      overflowX="auto"
                      fontSize="sm"
                    >
                      {JSON.stringify(formData, null, 2)}
                    </Code>
                  </TabPanel>
                </TabPanels>
              </Tabs>
            </CardBody>
          </Card>
        )}
      </VStack>
    </Container>
  )
}
