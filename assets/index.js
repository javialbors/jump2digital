const api_path = "http://localhost:8080/j2d/api";

function submitRegister(form) {
    let username = form.elements.username.value;
    let password = form.elements.password.value;

    let query = buildQueryParams({ username: username, password: password });

    sendRequest(form, "POST", "/register", query);

    return false;
}

function submitLogin(form) {
    let username = form.elements.username.value;
    let password = form.elements.password.value;

    let query = buildQueryParams({ username: username, password: password });

    sendRequest(form, "POST", "/login", query);

    return false;
}

function skinsAvailable(form) {
  sendRequest(form, "GET", "/skins/available");

  return false;
}

function getSkinByID(form) {
  let skinID = form.elements.skin.value;

  sendRequest(form, "GET", "/skin/getskin/" + skinID);

  return false;
}

function buySkin(form) {
    let skin = form.elements.skin.value;
    let apiKey = form.elements.apiKey.value;

    let query = buildQueryParams({ skinID: skin, apiKey: apiKey });

    sendRequest(form, "POST", "/skins/buy", query);

    return false;
}

function userSkins(form) {
    let apiKey = form.elements.apiKey.value;

    let query = buildQueryParams({ apiKey: apiKey });

    sendRequest(form, "GET", "/skins/myskins", query);

    return false;
}

function changeSkinColor(form) {
  let skin = form.elements.skin.value;
  let hex = form.elements.hex.value;
  let apiKey = form.elements.apiKey.value;

  let query = buildQueryParams({ skinID: skin, color: hex, apiKey: apiKey });

  sendRequest(form, "PUT", "/skins/color", query);

  return false;
}

function deleteUserSkin(form) {
  let skinID = form.elements.skin.value;
  let apiKey = form.elements.apiKey.value;

  let query = buildQueryParams({ apiKey: apiKey });

  sendRequest(form, "DELETE", "/skins/delete/" + skinID, query);

  return false;
}





function buildQueryParams(params) {
  return Object.keys(params)
    .map(key => encodeURIComponent(key) + "=" + encodeURIComponent(params[key]))
    .join("&");
}

function sendRequest(form, method, endpoint, data) {
  let textarea = form.nextElementSibling.querySelector("textarea");
  textarea.style.display = "block";
  textarea.value = "Loading...";

  HTTPRequest(method, api_path + endpoint, data)
  .then((response) => {
    let json = JSON.stringify(JSON.parse(response), null, 4);
    textarea.value = json;
    textarea.rows = json.split("\n").length > 15 ? 15 : json.split("\n").length + 1;
  })
  .catch((error) => {
    let json = JSON.stringify(JSON.parse(error), null, 4)
    textarea.value = json;
    textarea.rows = json.split("\n").length + 1;
  });
}

function HTTPRequest(method, url, data) {
  return new Promise((resolve, reject) => {
    let request = new XMLHttpRequest();

    let requestURL = url + (data == undefined ? "" : "?" + data);

    request.open(method, requestURL, true);
    request.send();

    request.onload = () => {
      if (request.status >= 200 && request.status < 300) {
        resolve(request.responseText);
      } else {
        reject(request.responseText);
      }
    }

    request.onerror = function() {
      reject(new Error("Network error"));
    };
  });
}

document.addEventListener("DOMContentLoaded", function() {
  document.querySelectorAll(".path-parameter").forEach((i) => {
    i.addEventListener("input", (e) => {
      let input = e.target;

      let em = 1 + input.value.length;

      input.style.width = em + "em";

    });
  });
});
