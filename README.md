# Client-Initiated Backchannel Authentication with Keycloak

This is a demonstration of CIBA authentication flow with Keycloak as authorization server.

```mermaid
sequenceDiagram
    ciba-consumer->>+Keycloak: Authentication request (scope, username, message)
    Keycloak->>ciba-relying-party: Authentication request data to backchannel URI
    ciba-relying-party-->>Keycloak: HTTP 201
    Keycloak-->>ciba-consumer: auth_req_id, expires_in, interval
    ciba-relying-party->>ciba-authenticator: Notification
    ciba-authenticator->>ciba-relying-party: Accept/Reject
    ciba-relying-party->>Keycloak: Status (SUCCEED/UNAUTHORIZED/CANCELLED)
    ciba-consumer->>Keycloak: Poll status using auth_req_id
    Keycloak-->>-ciba-consumer: Issue access token, ID token, refresh token
```

## Requirements

- JDK 21
- Docker
- Docker Compose

## Running

```shell
./gradlew bootJar
docker compose up --build
```

## Usage

Go to http://localhost:8080, sign in using "admin" as username and password.
Then switch to `ciba-demo` realm and create a new user under Users section.

<img width="1032" alt="image" src="https://github.com/user-attachments/assets/5414cdb4-5d73-403e-84c3-b3ad9bf0e940" />

Then open http://localhost:8002/ and http://localhost:8001/ in separate web browser tabs or windows.

Submit a form in http://localhost:8001/, which will trigger new authentication request.
The page will periodically refresh to poll authentication status.

<img width="581" alt="image" src="https://github.com/user-attachments/assets/5771cb27-666d-4c30-93f2-933894e45139" />

<img width="543" alt="image" src="https://github.com/user-attachments/assets/f42a7127-2159-4309-be0f-cc7b79ad6821" />

Then, switch to already opened http://localhost:8002/ tab. You will see a new request
appear in the table with 2 buttons - Accept and Reject. Click one of the buttons

<img width="1083" alt="image" src="https://github.com/user-attachments/assets/498e688b-76f2-4787-b73e-c8b1cc0a000a" />

Switch back to the polling tab to see the authentication status.

<img width="1084" alt="image" src="https://github.com/user-attachments/assets/ddccbd78-1663-4c92-844f-2882ff3d4296" />

