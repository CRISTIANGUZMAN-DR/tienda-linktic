import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import ErrorMessage from '../ErrorMessage.vue'

describe('ErrorMessage', () => {
  it('muestra el mensaje de error', () => {
    const wrapper = mount(ErrorMessage, {
      props: { message: 'Error de conexión' },
    })
    expect(wrapper.text()).toContain('Error de conexión')
  })

  it('muestra botón de reintento si se pasa onRetry', async () => {
    const onRetry = vi.fn()
    const wrapper = mount(ErrorMessage, {
      props: { message: 'Error', onRetry },
    })
    expect(wrapper.find('button').exists()).toBe(true)
    await wrapper.find('button').trigger('click')
    expect(onRetry).toHaveBeenCalled()
  })

  it('no muestra botón de reintento si no se pasa onRetry', () => {
    const wrapper = mount(ErrorMessage, {
      props: { message: 'Error' },
    })
    expect(wrapper.find('button').exists()).toBe(false)
  })
})
