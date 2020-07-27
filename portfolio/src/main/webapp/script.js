// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the 'License');
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an 'AS IS' BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/** Fetches messages from the server and adds them to the DOM. */
function loadMessages() {
  var number_messages = document.getElementById('number-messages').value;
  fetch('/list-messages?number_messages='+number_messages).then(response => response.json()).then((messages) => {
    const messageListElement = document.getElementById('messages-list');
    messageListElement.innerHTML = '';
    fetch('/user-info').then(response => response.json()).then((userInfo) => {
      messages.forEach((message) => {
        messageListElement.appendChild(createMessageElement(message, userInfo['uid']));
      }); 
    });
  });
}

/** Validate the form and add warning if something wrong. */
function processMessageForm() {
  document.getElementById('message-warn-container').innerText = '';
  
  var text = document.getElementById('text').value;

  if (text == null || text.trim().length == 0) {
    document.getElementById('message-warn-container').innerText = 'Please write a message.';
    return false;
  }
  return true;
}

function processNicknameForm() {
  document.getElementById('nickname-warn-container').innerText = '';
  
  var nickname = document.getElementById('nickname').value;

  if (nickname == null || nickname.trim().length == 0) {
    document.getElementById('nickname-warn-container').innerText = 'Please write a nickname.';
    return false;
  }
  return true;
}

/** Creates an element that represents a message, including its delete button. */
function createMessageElement(message, current_uid) {
  const messageElement = document.createElement('li');

  const timeElement = document.createElement('span');
  timeElement.setAttribute('class', 'timestamp');
  timeElement.innerText = formatTimestamp(message.timestamp);

  const nameElement = document.createElement('span');
  nameElement.setAttribute('class', 'nickname');
  nameElement.innerText = message.nickname;

  const textElement = document.createElement('span');
  textElement.setAttribute('class', 'text');
  textElement.innerText = message.text;

  if (message.uid == current_uid) {
    const deleteButtonElement = document.createElement('button');
    deleteButtonElement.setAttribute('class', 'delete');
    deleteButtonElement.innerHTML='<img src="images/trash.svg">';
    deleteButtonElement.addEventListener('click', () => {
    deleteMesage(message);

    // Remove the message from the DOM.
    messageElement.remove();
    });
    messageElement.appendChild(deleteButtonElement);
  }
  messageElement.appendChild(timeElement);
  messageElement.appendChild(nameElement);
  messageElement.appendChild(textElement);
  
  return messageElement;
}

/** Tells the server to delete the message. */
function deleteMesage(message) {
  const params = new URLSearchParams();
  params.append('id', message.id);
  params.append('uid', message.uid);
  fetch('/delete-message', {method: 'POST', body: params});
  loadMessages();
}

function loadCV() {
  const CVContainer = document.getElementById('cv-container');
  CVContainer.innerText = 'Opening...';
}

function loadNicknameForm() {
  fetch('/user-info').then(response => response.json()).then((userInfo) => {
    var isLoggedIn = userInfo["is-logged-in"];

    if (!isLoggedIn) {
      document.getElementById('nickname-container').hidden = true;
      document.getElementById('log-container').innerHTML = `<p>Want to choose a nickname? Please, <a href=${userInfo["login-url"]}>login</a></p>`;
    } else {
      document.getElementById('nickname-container').hidden = false;
      document.getElementById('log-container').innerHTML = `<p>You can <a href=${userInfo["logout-url"]}>logout</a></p>`;
      
      var nickname = userInfo["nickname"];
      if (nickname != '' && nickname != null) {
        document.getElementById('nickname').value = nickname;
      }
    }
  });
}

function loadMessageForm() {
  fetch('/user-info').then(response => response.json()).then((userInfo) => {
    var isLoggedIn = userInfo["is-logged-in"];

    if (!isLoggedIn) {
      document.getElementById('message-container').hidden = true;
      document.getElementById('log-container').innerHTML = `<p>Want to send a message? Please, <a href=${userInfo["login-url"]}>login</a></p>`;
    } else {
      document.getElementById('message-container').hidden = false;
      document.getElementById('log-container').innerHTML = `<p>You can <a href=${userInfo["logout-url"]}>logout</a></p>`;
      
      var nickname = userInfo["nickname"];
      if (nickname != '' && nickname != null) {
        document.getElementById('nickname-container').innerHTML = `<p><span class="nickname">${nickname}</span>Change <a href="/nickname.html">here</a>.</p>`;
      } else {
        document.getElementById('nickname-container').innerHTML = `<p>You don't have a nickname. Choose <a href="/nickname.html">here</a>.</p>`;
      }
    }
  });
}

/**
 * Adds a number of fetches to the page.
 */

function getData() {
  fetch('/data').then(response => response.json()).then((count) => {
    document.getElementById('data-container').innerText = count;
  });
}

/**
 * Adds a random placeholder.
 */
function getRandomPlaceholder(elementName) {
  const placeholders = ['I love cheesecake jelly-o sweet.', 'I love gingerbread bear claw marshmallow toffee cookie donut marzipan jelly-o.', 'Cupcake jujubes donut croissant I love.'];

  // Pick a random placeholder.
  const placeholder = placeholders[Math.floor(Math.random() * placeholders.length)];

  // Add it to the page.
  document.getElementsByName(elementName)[0].placeholder = placeholder;
}

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings = ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!', 'Привет мир!', 'Բարեւ աշխարհ!'];
  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

function formatTimestamp(timestamp) {
  var date = new Date(timestamp);
  return date.toLocaleString();
}

google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

/** Creates a chart and adds it to the page. */
function drawChart() {
  const data = new google.visualization.DataTable();
  data.addColumn('string', 'Animal');
  data.addColumn('number', 'Count');
        data.addRows([
          ['Lions', 10],
          ['Tigers', 5],
          ['Bears', 15]
        ]);

  const options = {
    'title': 'Zoo Animals',
    'width':500,
    'height':400
  };

  const chart = new google.visualization.PieChart(
      document.getElementById('chart-container'));
  chart.draw(data, options);
}
