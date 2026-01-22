import { customizeValidator } from '@rjsf/validator-ajv8'

/**
 * Validates an MQTT topic (no wildcards allowed)
 */
const validateMqttTopic = (topic: string): boolean => {
  if (!topic || topic.length === 0) {
    return false
  }
  // Topics cannot contain wildcards # or +
  if (topic.includes('#') || topic.includes('+')) {
    return false
  }
  // Topics cannot contain null character
  if (topic.includes('\u0000')) {
    return false
  }
  return true
}

/**
 * Validates an MQTT topic filter (wildcards allowed)
 */
const validateMqttTopicFilter = (filter: string): boolean => {
  if (!filter || filter.length === 0) {
    return false
  }
  // Null character not allowed
  if (filter.includes('\u0000')) {
    return false
  }
  // # must be the last character and preceded by /
  const hashIndex = filter.indexOf('#')
  if (hashIndex !== -1) {
    if (hashIndex !== filter.length - 1) {
      return false
    }
    if (hashIndex > 0 && filter[hashIndex - 1] !== '/') {
      return false
    }
  }
  // + must be a complete level (surrounded by / or at start/end)
  const parts = filter.split('/')
  for (const part of parts) {
    if (part.includes('+') && part !== '+') {
      return false
    }
  }
  return true
}

/**
 * Validates an MQTT tag (same as topic - no wildcards)
 */
const validateMqttTag = (tag: string): boolean => {
  return validateMqttTopic(tag)
}

/**
 * Validates a JWT token format
 */
const validateJwt = (token: string): boolean => {
  if (!token) return false
  const parts = token.split('.')
  return parts.length === 3
}

/**
 * Custom RJSF validator with HiveMQ Edge custom formats
 */
export const customFormatsValidator = customizeValidator({
  customFormats: {
    // MQTT formats
    'mqtt-topic': validateMqttTopic,
    'mqtt-topic-filter': validateMqttTopicFilter,
    'mqtt-tag': validateMqttTag,

    // JWT format
    'jwt': validateJwt,

    // Passthrough formats (validation done elsewhere or not needed for testing)
    'boolean': () => true,
    'interpolation': () => true,
    'identifier': () => true,
    'application/octet-stream': () => true,
  },
})
