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

public class Message {
  private final long id;
  private final String uid;
  private final String nickname;
  private final String text;
  private final long timestamp;

  public Message(long id, String uid, String nickname, String text, long timestamp) {
    this.id = id;
    this.uid = uid;
    this.nickname = nickname;
    this.text = text;
    this.timestamp = timestamp;
  }
}
