# SaaS Pedidos - Operacao

## Rodar local com Docker

1. Copie `.env.example` para `.env` se quiser customizar variaveis.
2. Execute:

```bash
docker compose up --build
```

3. Acesse:
- API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui/index.html`
- Healthcheck: `http://localhost:8080/actuator/health`

## Rodar local sem Docker

Pre-requisitos:
- Java 21
- PostgreSQL

Comandos:

```bash
./mvnw test
./mvnw spring-boot:run
```

## Variaveis importantes

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_JWT_SECRET`
- `APP_JWT_EXPIRATION_MS`
- `APP_JWT_REFRESH_EXPIRATION_MS`
- `APP_PASSWORD_RESET_EXPIRATION_MS`
- `APP_FRONTEND_RESET_PASSWORD_URL`
- `APP_MAIL_ENABLED`
- `APP_MAIL_FROM`
- `MAIL_HOST`
- `MAIL_PORT`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`

## Email real

Para usar email real:

1. Crie uma conta SMTP no provedor desejado.
2. Configure as variaveis `MAIL_*`.
3. Ative `APP_MAIL_ENABLED=true`.
4. Defina `APP_FRONTEND_RESET_PASSWORD_URL` apontando para a pagina real do frontend.

## GitHub Actions

O workflow esta em `.github/workflows/ci.yml`.

Para funcionar no GitHub:

1. Suba o repositorio.
2. Garanta que o branch principal seja `main` ou `master`.
3. O CI vai rodar testes automaticamente em push e pull request.

## Proximo passo fora do IntelliJ

Itens que dependem de infraestrutura externa:

1. Criar conta SMTP real.
2. Publicar banco PostgreSQL gerenciado ou VM com Postgres.
3. Fazer deploy da API em Render, Railway, Fly.io, VPS ou AWS.
4. Configurar dominio e HTTPS.
5. Configurar secrets do ambiente de producao.
