// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/** Fetches messages from the server and adds them to the DOM. */
function loadMessages() {
  fetch('/list-messages').then(response => response.json()).then((messages) => {
    const messageListElement = document.getElementById('messages-list');
    messages.forEach((message) => {
      messageListElement.appendChild(createMessageElement(message));
    })
  });
}

/** Validate the form and add warning if something wrong. */
function processMessageForm() {
  document.getElementById('warn-container').innerText = "";
  
  var name = document.getElementById('name').value;
  var text = document.getElementById('text').value;

  var warning = processWarning(name, text);
  if (warning.length != 0) {
    document.getElementById('warn-container').innerText = warning;
    return false;
  }

  return true;
}

/** Returns the warning if the message is empty or name is anonym. Return "" if everything OK. */
function processWarning(name, text) {
  var warning = "";
  if (name.trim().length == 0) {
    warning += "Please choose a name.\n";
  }
  if (text.trim().length == 0) {
    warning += "Please write a message.\n";
  }
  return warning;
}

/** Creates an element that represents a message, including its delete button. */
function createMessageElement(message) {
  const messageElement = document.createElement('li');
  messageElement.className = 'message';

  const timeElement = document.createElement('span');
  timeElement.setAttribute('class', 'timestamp');
  timeElement.innerText = formatTimestamp(message.timestamp);

  const nameElement = document.createElement('span');
  nameElement.setAttribute('class', 'name');
  nameElement.innerText = message.name;

  const textElement = document.createElement('span');
  textElement.setAttribute('class', 'text');
  textElement.innerText = message.text;

  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.addEventListener('click', () => {
    deleteMesage(message);

    // Remove the message from the DOM.
    messageElement.remove();
  });

  messageElement.appendChild(timeElement);
  messageElement.appendChild(nameElement);
  messageElement.appendChild(textElement);
  messageElement.appendChild(deleteButtonElement);
  return messageElement;
}

function formatTimestamp(timestamp) {
  var date = new Date(timestamp);
  return date.toLocaleString();
}

/** Tells the server to delete the message. */
function deleteMesage(message) {
  const params = new URLSearchParams();
  params.append('id', message.id);
  fetch('/delete-message', {method: 'POST', body: params});
}

/**
 * Adds a number of fetches to the page.
 */

function getData() {
  fetch('/data').then(response => response.json()).then((comment) => {
    document.getElementById('data-container').innerText = comment;
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
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}
