1. Cria o ficheiro `.env` baseado em `.env.example`
2. Faz build:
   ```bash
   docker-compose down
   docker-compose up -d --build
   ```
3. No IntelliJ:
    - Vai a Run > Edit Configurations > Remote JVM Debug
    - Host: `116.203.136.176`
    - Port: `5005`
    - Nome: Staywise Remote Debug

4. Mete breakpoints, clica em `Debug` e estás ligado 🔥

5. Abre o pgAdmin em: http://116.203.136.176:5050
    - Email: admin@staywise.com
    - Pass: admin
