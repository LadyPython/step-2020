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

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * <p>Note: The private variables in this class are converted into JSON.
 */

public class Message {
  private final String name;
  private final String text;
  private final String timestamp;

  public Message(String name, String text) { 
    this.name = name;
    this.text = text;
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    this.timestamp = formatter.format(date);
  }

  public boolean hasName() { 
    return name != null && !name.trim().isEmpty();
  }

  public boolean hasText() { 
    return text != null && !text.trim().isEmpty();
  }
}
