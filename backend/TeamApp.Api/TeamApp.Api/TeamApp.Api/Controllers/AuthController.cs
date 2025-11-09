using Microsoft.AspNetCore.Mvc;
using TeamApp.Application;


namespace TeamApp.Api.Controllers;

[ApiController, Route("api/auth")]
public class AuthController : ControllerBase
{
    private readonly AuthService _svc;
    public AuthController(AuthService svc) { _svc = svc; }

    [HttpPost("register")]
    public async Task<ActionResult<AuthResponse>> Register(RegisterRequest req) => Ok(await _svc.RegisterAsync(req));

    [HttpPost("login")]
    public async Task<ActionResult<AuthResponse>> Login(LoginRequest req) => Ok(await _svc.LoginAsync(req));
}
