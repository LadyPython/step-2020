// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.data;

/**
 * <p>Note: The private variables in this class are converted into JSON.
 */

public class Message {
  String name;
  String text;

  public Message(String name, String text) { 
    this.name = name;
    this.text = text;
  }

  public boolean nameIsEmpty() { 
    return name.length() == 0;
  }

  public boolean textIsEmpty() { 
    return text.length() == 0;
  }
}
