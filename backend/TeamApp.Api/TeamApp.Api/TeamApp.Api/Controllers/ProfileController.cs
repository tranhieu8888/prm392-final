using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using TeamApp.Application;
using TeamApp.sln.Utils;

namespace TeamApp.Api.Controllers;

[ApiController, Route("api/me")]
[Authorize]
public class ProfileController : ControllerBase
{
    private readonly ProfileService _svc;
    public ProfileController(ProfileService svc) { _svc = svc; }

    [HttpGet]
    public Task<UserDto> Me() => _svc.MeAsync(HttpContext.UserId());

    public record UpdateProfileBody(string FullName, string? AvatarUrl);
    [HttpPut("profile")]
    public async Task<IActionResult> UpdateProfile([FromBody] UpdateProfileBody body)
    {
        await _svc.UpdateProfileAsync(HttpContext.UserId(), body.FullName, body.AvatarUrl);
        return NoContent();
    }

    public record ChangePasswordBody(string CurrentPassword, string NewPassword);
    [HttpPut("password")]
    public async Task<IActionResult> ChangePassword([FromBody] ChangePasswordBody body)
    {
        await _svc.ChangePasswordAsync(HttpContext.UserId(), body.CurrentPassword, body.NewPassword);
        return NoContent();
    }
}
