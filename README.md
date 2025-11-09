dotnet ef migrations add <TênMigration> -p TeamApp.Infrastructure -s TeamApp.Api
dotnet ef database update -p TeamApp.Infrastructure -s TeamApp.Api
