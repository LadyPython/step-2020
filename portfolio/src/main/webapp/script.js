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

function getChat() {
  fetch('/send-message').then(response => response.json()).then((data) => {
    // Build the list of history entries.
    const historyEl = document.getElementById('history');
    data.chat.history.forEach((message) => {
      historyEl.appendChild(createListElement(message.name, "name"));
      historyEl.appendChild(createListElement(message.text, "text"));
    });

    document.getElementById('warn-container').innerText = data.warning;
  });
}

/** Creates an <li> element containing text. */
function createListElement(text, liClass) {
  const liElement = document.createElement('li');
  liElement.setAttribute("class", liClass);
  liElement.innerText = text
  return liElement;
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
