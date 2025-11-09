// Program.cs
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using System.Text;
using TeamApp.Api;
using TeamApp.Api.Realtime;
using TeamApp.Application;
using TeamApp.Application.RealTime;
using TeamApp.Infrastructure;

var builder = WebApplication.CreateBuilder(args);
var cfg = builder.Configuration;

// ========== EF Core ==========
builder.Services.AddDbContext<AppDbContext>(opt =>
    opt.UseSqlServer(cfg.GetConnectionString("Default")));

// ========== Services ==========
builder.Services.AddScoped<AuthService>();
builder.Services.AddScoped<ProjectService>();
builder.Services.AddScoped<TaskService>();
builder.Services.AddScoped<JoinRequestService>();
builder.Services.AddScoped<SearchService>();
builder.Services.AddScoped<CalendarService>();
builder.Services.AddScoped<NotificationService>();
builder.Services.AddScoped<DeviceService>();
builder.Services.AddHttpClient<FcmSender>();
builder.Services.AddScoped<ProfileService>();
builder.Services.AddScoped<CommentService>();
builder.Services.AddScoped<ConversationService>();
builder.Services.AddScoped<MessageService>();
builder.Services.AddScoped<IRealtimeNotifier, SignalRRealtimeNotifier>();

builder.Services.AddSignalR();
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();

// Swagger + JWT (optional nhưng hữu ích khi test)
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new() { Title = "TeamApp API", Version = "v1" });
    var jwtScheme = new Microsoft.OpenApi.Models.OpenApiSecurityScheme
    {
        Name = "Authorization",
        Type = Microsoft.OpenApi.Models.SecuritySchemeType.Http,
        Scheme = "bearer",
        BearerFormat = "JWT",
        In = Microsoft.OpenApi.Models.ParameterLocation.Header,
        Description = "Nhập token theo dạng: Bearer {token}"
    };
    c.AddSecurityDefinition("Bearer", jwtScheme);
    c.AddSecurityRequirement(new()
    {
        { jwtScheme, Array.Empty<string>() }
    });
});

// ========== JWT ==========
var keyBytes = JwtKeyHelper.GetKeyBytes(cfg);
var signingKey = new SymmetricSecurityKey(keyBytes);
var issuer = cfg["Jwt:Issuer"] ?? "TeamApp";
var audience = cfg["Jwt:Audience"] ?? "TeamApp";

builder.Services
    .AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.RequireHttpsMetadata = true;
        options.SaveToken = true;
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidateAudience = true,
            ValidateIssuerSigningKey = true,
            ValidIssuer = issuer,
            ValidAudience = audience,
            IssuerSigningKey = signingKey,
            ClockSkew = TimeSpan.Zero
        };

        // Cho phép SignalR nhận token qua query "?access_token="
        options.Events = new JwtBearerEvents
        {
            OnMessageReceived = context =>
            {
                var accessToken = context.Request.Query["access_token"];
                var path = context.HttpContext.Request.Path;
                if (!string.IsNullOrEmpty(accessToken) && path.StartsWithSegments("/hubs/chat"))
                    context.Token = accessToken;
                return Task.CompletedTask;
            }
        };
    });

builder.Services.AddAuthorization();

var app = builder.Build();

// ========== Middleware thứ tự đúng ==========
if (app.Environment.IsDevelopment())
{
    app.UseDeveloperExceptionPage();
    app.UseSwagger();
    app.UseSwaggerUI();
}

//app.UseHttpsRedirection();

app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();
app.MapHub<ChatHub>("/hubs/chat");

app.Run();
