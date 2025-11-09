using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

using TeamApp.Application;

using TeamApp.sln.Utils;

namespace TeamApp.Api.Controllers;

[ApiController, Route("api/devices")]
public class DevicesController : ControllerBase
{
    private readonly DeviceService _svc;
    public DevicesController(DeviceService svc) { _svc = svc; }

    [HttpPost, Authorize]
    public async Task<IActionResult> Save(SaveDeviceTokenRequest req)
    {
        await _svc.SaveAsync(HttpContext.UserId(), req.FcmToken, req.Platform);
        return NoContent();
    }
}
