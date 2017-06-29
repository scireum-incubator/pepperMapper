package controller;

import com.alibaba.fastjson.JSONObject;
import io.netty.handler.codec.http.HttpResponseStatus;
import sirius.kernel.di.std.Register;
import sirius.kernel.health.HandledException;
import sirius.web.controller.BasicController;
import sirius.web.controller.Controller;
import sirius.web.controller.DefaultRoute;
import sirius.web.controller.Routed;
import sirius.web.http.WebContext;
import sirius.web.security.LoginRequired;
import sirius.web.templates.ExcelExport;


@Register(classes = Controller.class)
public class ConvertController extends BasicController {
    @Override
    public void onError(WebContext webContext, HandledException e) {
        webContext.respondWith().error(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }

    @DefaultRoute
    @Routed("/convert")
    public void convert(final WebContext ctx) {
        if (ctx.isPOST() && ctx.get("input").isFilled()) {
            convertJSONToXls(ctx.get("input").asString()).writeResponseTo("mapping.xls", ctx);
        }
        ctx.respondWith().template("view/input.html");
    }

    private ExcelExport convertJSONToXls(String input) {
        ExcelExport excelExport = new ExcelExport();
        JSONObject index = JSONObject.parseObject(input);
        index.forEach((key1, value1) -> {
            excelExport.addRow();
            JSONObject entity = index.getJSONObject(key1);
            excelExport.addRow(key1, entity.getJSONObject("routing").get("path"));
            JSONObject fields = entity.getJSONObject("properties");
            fields.forEach((key2, value2) -> {
                JSONObject field = fields.getJSONObject(key2);
                excelExport.addRow(key2, field.get("type"), field.get("index"), field.get("store"), field.get("include_in_all"), field.get("format"));
            });
        });
        return excelExport;
    }
}