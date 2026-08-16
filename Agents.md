# OrderOrbit Development Instructions

## Project

OrderOrbit is the procurement microservice of the Hoodle system.

It contains procurement functionality such as:
- Purchase Requisition (PRQ)
- Purchase Order (PO)
- Vendor

## Workspace Structure

The development workspace also contains a separate microservice:

- hoodle-auth
    - Authentication
    - Users
    - Roles
    - Authorization

OrderOrbit and hoodle-auth are separate microservices/projects.

## Current Development Target

The current feature being developed is the Vendor module in OrderOrbit.

For Vendor development:

- OrderOrbit is the primary project.
- hoodle-auth is reference-only unless explicitly stated otherwise.
- Do not modify hoodle-auth unless explicitly requested.

## Reference Patterns

When useful, inspect hoodle-auth for established patterns, especially:

- User/Role APIs
- search
- pagination
- DTO/projection patterns
- avoiding N+1 queries
- query optimization

Do not copy implementations blindly. Follow OrderOrbit's existing conventions first.

## Scope

When implementing a requested feature:

- Modify only files necessary for the requested feature.
- Do not refactor unrelated code.
- Do not introduce new dependencies unless necessary.
- Do not change authentication or authorization architecture unless explicitly requested.
- Do not modify other microservices unless explicitly requested.

## Database and Queries

Prefer:

- database-level pagination
- efficient queries
- DTO projections where appropriate
- avoiding N+1 queries
- appropriate indexes for frequently searched fields

Do not load complete entities when only a small subset of fields is required.

## API Design

Follow existing OrderOrbit conventions for:

- controllers
- DTOs
- services
- repositories
- validation
- responses
- exception handling
- pagination
- naming

Prefer existing project patterns over introducing new abstractions.

## Multi-tenancy

Tenant-owned data must respect the existing tenant isolation mechanism.

Never allow data belonging to one tenant to be accessed or modified by another tenant.

## Testing

After implementation:

- run relevant tests
- run compilation/build
- fix issues caused by the implementation
- do not silently ignore test failures

## Git

Do not commit, push, create branches, or create/merge pull requests unless explicitly requested by the user.

Before creating a PR:

- run relevant tests
- verify the final diff
- summarize the changes
- identify any remaining concerns

## Agent Behavior

Before implementing a feature:

1. Inspect the relevant existing code.
2. Identify established patterns.
3. Create a concise implementation plan when the task is non-trivial.
4. Implement only the requested scope.
5. Validate the implementation.
6. Report files changed, tests run, and any remaining issues.