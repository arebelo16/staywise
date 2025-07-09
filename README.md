1. Create `.env` file based on `.env.example`
2. Build:
   ```bash
   docker-compose down
   docker-compose up -d --build
   ```
3. IntelliJ:
    - Run > Edit Configurations > Remote JVM Debug
    - Host: `116.203.136.176`
    - Port: `5005`
    - Nome: Domiledge Server Remote Debug

5. pgAdmin: http://116.203.136.176:5050
    - Email: {PGADMIN_EMAIL} defined in .env
    - Pass: {PGADMIN_PASSWORD} defined in .env
