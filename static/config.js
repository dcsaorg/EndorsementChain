function stylePage(){
    document.getElementById("pageTitle").innerHTML = "The eBL Platform";
    document.getElementById("tabTitle").innerHTML = "The eBL Platform";
}

var generateMenu = function(targetDiv) {
    fetch("menu.json").then(fileData => {
        fileData.text().then(data => {
            const menu = JSON.parse(data);
            var topBar = document.getElementById(targetDiv);
            Object.keys(menu).forEach(function(mainMenuItemName,i) {
                var newDropdown = document.createElement("li");
                newDropdown.setAttribute("class", "nav-item dropdown");
                var newA = document.createElement("a");
                newA.setAttribute("class","nav-link dropdown-toggle");
                newA.setAttribute("href", "#");
                newA.setAttribute("id", "navbarDropdown" + i);
                newA.setAttribute("role", "button");
                newA.setAttribute("data-bs-toggle", "dropdown");
                newA.setAttribute("aria-expanded", "false");
                newA.innerHTML = mainMenuItemName;
                newDropdown.appendChild(newA);
                var newUl = document.createElement("ul");
                newUl.setAttribute("class", "dropdown-menu");
                newUl.setAttribute("aria-labelledby", "navbarDropdown"+i);
                menu[mainMenuItemName].forEach(function(listItem) {
                    var newMenuOption = document.createElement("li");
                    var newMenuOptionA = document.createElement("a");
                    newMenuOptionA.setAttribute("class", "dropdown-item");
                    newMenuOptionA.setAttribute("href", Object.values(listItem)[0]);
                    newMenuOptionA.innerHTML = Object.keys(listItem)[0];
                    newMenuOption.appendChild(newMenuOptionA);
                    newUl.appendChild(newMenuOption);
                });
                newDropdown.appendChild(newUl);
                topBar.appendChild(newDropdown);
            });
        })
    })
};
