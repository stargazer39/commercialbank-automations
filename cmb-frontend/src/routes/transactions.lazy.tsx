import * as React from 'react'
import { createLazyFileRoute } from '@tanstack/react-router'

export const Route = createLazyFileRoute('/transactions')({
  component: RouteComponent,
})

function RouteComponent() {
  return 'Hello /transactions!'
}
