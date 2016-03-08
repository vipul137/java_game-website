import java.util.*;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import java.lang.Thread;

import static spark.Spark.*;

public class App {
  public static void main(String[] args) {
    staticFileLocation("/public");
    String layout = "templates/layout.vtl";

    get("/", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/index.vtl");
      return new ModelAndView (model, layout);
    }, new VelocityTemplateEngine());

    get("/takeTwo", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      boolean incorrectUsername = request.session().attribute("incorrectUsername");
      boolean incorrectPassword = request.session().attribute("incorrectPassword");
      User user = request.session().attribute("user");
      model.put("incorrectUsername", incorrectUsername);
      model.put("incorrectPassword", incorrectPassword);
      model.put("user", user);
      model.put("template", "templates/index.vtl");
      return new ModelAndView (model, layout);
    }, new VelocityTemplateEngine());

    get("/signUp", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/sign_up.vtl");
      return new ModelAndView (model, layout);
    }, new VelocityTemplateEngine());

    post("/checkPassword", (request, response) -> {
      String inputPassword = request.queryParams("password");
      String inputName = request.queryParams("name");
      User user = User.findByName(inputName);
      if(user != null) {
        if(user.getPassword().equals(inputPassword)) {
          request.session().attribute("incorrectPassword", false);
          request.session().attribute("incorrectUsername", false);
          request.session().attribute("user", user);
          response.redirect("/games");
          return null;
        } else {
          request.session().attribute("incorrectPassword", true);
          request.session().attribute("incorrectUsername", false);
          request.session().attribute("user", user);
          response.redirect("/takeTwo");
          return null;
        }
      }
      request.session().attribute("incorrectPassword", false);
      request.session().attribute("incorrectUsername", true);
      request.session().attribute("user", null);
      response.redirect("/takeTwo");
      return null;
    });

    post("/signedUp", (request, response) -> {
      String inputPassword = request.queryParams("password");
      String inputName = request.queryParams("name");
      String inputPasswordHint = request.queryParams("passwordhint");
      if(inputName.trim().length() > 0 && inputPassword.trim().length() > 0) {
        User user = new User(inputName, inputPassword, "user");
        user.save();
        if(inputPasswordHint.length() > 0) {
          user.assignPasswordHint(inputPasswordHint);
        }
      }
      response.redirect("/");
      return null;
    });

    get("/games", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      User user = request.session().attribute("user");
      model.put("user", user);
      model.put("template", "templates/games.vtl");
      return new ModelAndView (model, layout);
    }, new VelocityTemplateEngine());

    get("/profile", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      User user = request.session().attribute("user");
      model.put("user", user);
      model.put("template", "templates/profile.vtl");
      return new ModelAndView (model, layout);
    }, new VelocityTemplateEngine());

    post("/updateImage", (request, response) -> {
      String imageUrl = request.queryParams("profilePic");
      User user = request.session().attribute("user");
      user.assignPorfilepic(imageUrl);
      response.redirect("/profile");
      return null;
    });

    post("/changePassword", (request, response) -> {
      String updatedPassword = request.queryParams("updatePassword");
      User user = request.session().attribute("user");
      user.updatePassword(updatedPassword);
      response.redirect("/profile");
      return null;
    });

    // get("/replay", (request, response) -> {
    //   HashMap<String, Object> model = new HashMap<String, Object>();
    //   List<Turn> turns = Turn.all();
    //   String color = newTurn.getGeneratedColor();
    //   if(color.equals("red")) {
    //     newTurn.updateShownStatus();
    //     response.redirect("/red");
    //     return null;
    //   }
    //   response.redirect("/play");
    //   return null;
    // });
    //
    // get("/red", (request, response) -> {
    //   HashMap<String, Object> model = new HashMap<String, Object>();
    //   model.put("template", "templates/red.vtl");
    //   return new ModelAndView (model, layout);
    // }, new VelocityTemplateEngine());
    //
    //
    // get("/index4", (request, response) -> {
    //   HashMap<String, Object> model = new HashMap<String, Object>();
    //   model.put("template", "templates/index4.vtl");
    //   return new ModelAndView (model, layout);
    // }, new VelocityTemplateEngine());

  } //end of main

  // public static void timer() {
  //
  //   long startTime = System.currentTimeMillis(); //fetch starting time
  //   while(false||(System.currentTimeMillis()-startTime)<5000)
  //   {
  //       get("/")
  //   }
  // }

}
