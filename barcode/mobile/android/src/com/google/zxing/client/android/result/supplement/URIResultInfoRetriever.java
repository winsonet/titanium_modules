/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android.result.supplement;

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;
import com.google.zxing.client.android.HttpHelper;
import com.google.zxing.client.android.history.HistoryManager;
import ti.barcode.RHelper;
import com.google.zxing.client.result.URIParsedResult;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

final class URIResultInfoRetriever extends SupplementalInfoRetriever {

  private static final int MAX_REDIRECTS = 5;

  private final URIParsedResult result;
  private final String redirectString;

  URIResultInfoRetriever(TextView textView,
                         URIParsedResult result,
                         Handler handler,
                         HistoryManager historyManager,
                         Context context) {
    super(textView, handler, historyManager);
    redirectString = context.getString(RHelper.getString("msg_redirect"));
    this.result = result;
  }

  @Override
  void retrieveSupplementalInfo() throws IOException, InterruptedException {
    URI oldURI;
    try {
      oldURI = new URI(result.getURI());
    } catch (URISyntaxException e) {
      return;
    }
    URI newURI = HttpHelper.unredirect(oldURI);
    int count = 0;
    while (count++ < MAX_REDIRECTS && !oldURI.equals(newURI)) {
      append(result.getDisplayResult(), 
             null, 
             new String[] { redirectString + " : " + newURI }, 
             newURI.toString());
      oldURI = newURI;
      newURI = HttpHelper.unredirect(newURI);
    }
  }

}
