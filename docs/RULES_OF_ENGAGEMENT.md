# Rules of Engagement

Kesho Bank is an official Africahackon practice platform. It is a wholly
fictional mobile banking app created for authorized security-testing
practice. It is not a real bank or financial institution, and it contains no
real customer, account, or transaction data. Every record is synthetic.

The same notice is shown in-app (Rules of Engagement screen, reachable from
Settings at any time) and cannot be dismissed without acknowledging it.

## Who may test

Testing is permitted only for registered members of the Africahackon
community, against your own local instance of the app (and, if you choose to
run it, your own local instance of the optional backend), for the duration
of the program under which it is made available.

## What is in scope

The Kesho Bank Android app itself, and the optional local backend in
`backend/` if you choose to run it. Any vulnerability class you can
genuinely trigger against Kesho Bank's own functionality (its local
storage, its exported components, its network layer, its backend API) is
in scope for practice. The two companion PoC apps in
`companion-poc-apps/` are provided source-only for this purpose; do not
distribute built APKs of them.

## What is out of scope

- Any app, service, or account that isn't Kesho Bank or its own local backend.
- Any form of denial-of-service or availability-impacting testing.
- Uploading, submitting, or otherwise introducing real personal data of any kind.
- Testing against anything other than your own local, self-hosted instance.
  There is no shared or hosted Kesho Bank instance.

## Reporting & contact

For questions about scope, or to report an issue with the environment,
contact [academy@africahackon.com](mailto:academy@africahackon.com).
