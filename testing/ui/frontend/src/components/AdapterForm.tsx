import { FC, useState } from 'react'
import { Box, Button, VStack, Alert, AlertIcon, useToast } from '@chakra-ui/react'
import Form from '@rjsf/chakra-ui'
import type { RJSFSchema, UiSchema } from '@rjsf/utils'
import type { IChangeEvent } from '@rjsf/core'
import { ObjectFieldTemplate } from './rjsf/ObjectFieldTemplate'
import { customFormatsValidator } from '../validation/customFormats'

interface AdapterFormProps {
  schema: RJSFSchema
  uiSchema?: UiSchema
  formData?: Record<string, unknown>
  onChange?: (data: Record<string, unknown>) => void
}

export const AdapterForm: FC<AdapterFormProps> = ({
  schema,
  uiSchema,
  formData,
  onChange,
}) => {
  const toast = useToast()
  const [errors, setErrors] = useState<string[]>([])

  const handleChange = (e: IChangeEvent) => {
    onChange?.(e.formData as Record<string, unknown>)
    setErrors([])
  }

  const handleSubmit = () => {
    toast({
      title: 'Form Submitted',
      description: 'Configuration is valid!',
      status: 'success',
      duration: 3000,
      isClosable: true,
    })
  }

  const handleError = (errorList: unknown[]) => {
    const errorMessages = errorList.map((err: unknown) => {
      if (typeof err === 'object' && err !== null && 'message' in err) {
        return String((err as { message: string }).message)
      }
      return String(err)
    })
    setErrors(errorMessages)
  }

  return (
    <VStack spacing={4} align="stretch">
      {errors.length > 0 && (
        <Alert status="error">
          <AlertIcon />
          <Box>
            {errors.map((err, idx) => (
              <Box key={idx}>{err}</Box>
            ))}
          </Box>
        </Alert>
      )}

      <Form
        schema={schema}
        uiSchema={uiSchema}
        formData={formData}
        validator={customFormatsValidator}
        onChange={handleChange}
        onSubmit={handleSubmit}
        onError={handleError}
        templates={{
          ObjectFieldTemplate,
        }}
        showErrorList={false}
      >
        <Button type="submit" colorScheme="blue" mt={4}>
          Validate Configuration
        </Button>
      </Form>
    </VStack>
  )
}
