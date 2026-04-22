describe('Flujo completo: listar → detalle → compra', () => {
  beforeEach(() => {
    cy.visit('/login')
    cy.get('input[placeholder="admin"]').type('admin')
    cy.get('input[placeholder="••••••"]').type('admin')
    cy.get('button').click()
    cy.url().should('include', '/products')
  })

  it('lista productos correctamente', () => {
    cy.get('.grid').should('exist')
    cy.get('.grid > div').should('have.length.greaterThan', 0)
  })

  it('navega al detalle del primer producto', () => {
    cy.get('.grid > div').first().click()
    cy.url().should('include', '/products/')
    cy.contains('Inventario disponible').should('exist')
  })

  it('compra exitosa muestra feedback', () => {
    cy.get('.grid > div').first().click()
    cy.get('input[type="number"]').clear()
    cy.get('input[type="number"]').type('1')
    cy.get('button').contains('Comprar').click()
    cy.contains('Compra exitosa').should('exist')
  })

  it('stock insuficiente muestra error claro', () => {
    cy.get('.grid > div').first().click()
    cy.get('input[type="number"]').clear()
    cy.get('input[type="number"]').type('99999')
    cy.get('button').contains('Comprar').click()
    cy.contains('Conflicto').should('exist')
  })
})
