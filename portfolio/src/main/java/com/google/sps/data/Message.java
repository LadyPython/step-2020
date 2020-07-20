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

public class Message {
  private final long id;
  private final String name;
  private final String text;
  private final long timestamp;

  public Message(String name, String text) {
    this.id = 0;
    this.name = name;
    this.text = text;
    this.timestamp = 0;
  }

  public Message(long id, String name, String text, long timestamp) {
    this.id = id;
    this.name = name;
    this.text = text;
    this.timestamp = timestamp;
  }

  public String getName() { 
    return name;
  }

  public String getText() { 
    return text;
  }

  public long getTimestamp() { 
    return timestamp;
  }

  public boolean hasName() { 
    return name != null && !name.trim().isEmpty();
  }

  public boolean hasText() { 
    return text != null && !text.trim().isEmpty();
  }
}
