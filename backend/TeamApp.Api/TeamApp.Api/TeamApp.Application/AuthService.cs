// AuthService.cs
using BCrypt.Net;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using TeamApp.Domain;
using TeamApp.Infrastructure;
using System.Text;

namespace TeamApp.Application;

public class AuthService
{
    private readonly AppDbContext _db;
    private readonly IConfiguration _cfg;

    public AuthService(AppDbContext db, IConfiguration cfg)
    {
        _db = db;
        _cfg = cfg;
    }

    public async Task<AuthResponse> RegisterAsync(RegisterRequest req)
    {
        if (await _db.Users.AnyAsync(x => x.Email == req.Email))
            throw new Exception("Email đã tồn tại");

        var user = new User
        {
            FullName = req.FullName,
            Email = req.Email,
            PasswordHash = BCrypt.Net.BCrypt.HashPassword(req.Password),
            CreatedAt = DateTime.UtcNow
        };

        _db.Users.Add(user);
        await _db.SaveChangesAsync();

        var token = GenerateJwt(user);
        return new AuthResponse(token, new UserDto(user.Id, user.FullName, user.Email!, user.AvatarUrl));
    }

    public async Task<AuthResponse> LoginAsync(LoginRequest req)
    {
        var user = await _db.Users.FirstOrDefaultAsync(x => x.Email == req.Email);
        if (user == null || !BCrypt.Net.BCrypt.Verify(req.Password, user.PasswordHash))
            throw new Exception("Sai thông tin đăng nhập");

        user.LastLoginAt = DateTime.UtcNow;
        await _db.SaveChangesAsync();

        var token = GenerateJwt(user);
        return new AuthResponse(token, new UserDto(user.Id, user.FullName, user.Email!, user.AvatarUrl));
    }

    private string GenerateJwt(User u)
    {
        var issuer = _cfg["Jwt:Issuer"] ?? "TeamApp";
        var audience = _cfg["Jwt:Audience"] ?? "TeamApp";
        var minutes = int.TryParse(_cfg["Jwt:AccessTokenMinutes"], out var m) ? m : 30;

        byte[] keyBytes = JwtKeyHelper.GetKeyBytes(_cfg);
        var signingKey = new SymmetricSecurityKey(keyBytes);
        var creds = new SigningCredentials(signingKey, SecurityAlgorithms.HmacSha256);

        var claims = new List<Claim>
        {
            new(JwtRegisteredClaimNames.Sub, u.Id.ToString()),
            new(JwtRegisteredClaimNames.Email, u.Email ?? ""),
            new("name", u.FullName ?? ""),
            new(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString())
        };

        var token = new JwtSecurityToken(
            issuer: issuer,
            audience: audience,
            claims: claims,
            notBefore: DateTime.UtcNow,
            expires: DateTime.UtcNow.AddMinutes(minutes),
            signingCredentials: creds
        );

        return new JwtSecurityTokenHandler().WriteToken(token);
    }
}


