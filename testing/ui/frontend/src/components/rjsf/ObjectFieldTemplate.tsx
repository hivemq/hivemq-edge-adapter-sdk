import { FC, useState } from 'react'
import {
  Box,
  Tabs,
  TabList,
  TabPanels,
  Tab,
  TabPanel,
  Grid,
} from '@chakra-ui/react'
import type { ObjectFieldTemplateProps } from '@rjsf/utils'

interface TabConfig {
  id: string
  title: string
  properties: string[]
}

/**
 * Custom ObjectFieldTemplate that supports tab-based layout via ui:tabs.
 *
 * UI Schema format:
 * {
 *   "ui:tabs": [
 *     { "id": "general", "title": "General", "properties": ["id", "host", "port"] },
 *     { "id": "advanced", "title": "Advanced", "properties": ["timeout", "retries"] }
 *   ]
 * }
 */
export const ObjectFieldTemplate: FC<ObjectFieldTemplateProps> = (props) => {
  const { properties, uiSchema } = props
  const [tabIndex, setTabIndex] = useState(0)

  // Check if tabs are configured in UI schema
  const tabs = uiSchema?.['ui:tabs'] as TabConfig[] | undefined

  if (tabs && tabs.length > 0) {
    // Group properties by tab
    const propertyMap = new Map(properties.map(p => [p.name, p]))
    const tabbedProperties = new Set(tabs.flatMap(t => t.properties))
    const untabbedProperties = properties.filter(p => !tabbedProperties.has(p.name))

    return (
      <Box>
        <Tabs index={tabIndex} onChange={setTabIndex} variant="enclosed">
          <TabList>
            {tabs.map((tab) => (
              <Tab key={tab.id}>{tab.title}</Tab>
            ))}
            {untabbedProperties.length > 0 && <Tab>Other</Tab>}
          </TabList>

          <TabPanels>
            {tabs.map((tab) => (
              <TabPanel key={tab.id} px={0}>
                <Grid gap={4}>
                  {tab.properties
                    .map(propName => propertyMap.get(propName))
                    .filter(Boolean)
                    .map(prop => prop!.content)}
                </Grid>
              </TabPanel>
            ))}
            {untabbedProperties.length > 0 && (
              <TabPanel px={0}>
                <Grid gap={4}>
                  {untabbedProperties.map(prop => prop.content)}
                </Grid>
              </TabPanel>
            )}
          </TabPanels>
        </Tabs>
      </Box>
    )
  }

  // Default layout without tabs
  return (
    <Box>
      <Grid gap={4}>
        {properties.map((prop) => prop.content)}
      </Grid>
    </Box>
  )
}
