"use strict";var precacheConfig=[["/index.html","a9b99f058aa4f593adffc5a5a201e4b1"],["/static/css/main.30f8869c.css","a92544bc7d8036a64405f72d18fdda5e"],["/static/js/main.8c8f914e.js","cf9438078cb51d3fc2b5b0125c6078d5"],["/static/media/FlameHound Logo with Orange Background +Text@2x.fc4945ed.png","fc4945ed50ff2af8c2b7e51b0cc5bc23"],["/static/media/FlameHound Logo with Orange Background@2x.2e7336a2.png","2e7336a2cb2639f0b0a39210f1dafb66"],["/static/media/FlameHound Logo with Transparency@2x.7178ac2f.png","7178ac2f69e2b4260529c2f4d4743441"],["/static/media/FotoUser.2cab1ed4.png","2cab1ed48bfe4b4c97e034d467f16ae9"],["/static/media/IncendioCasa.afb394f0.jpg","afb394f04491c46ce14117df163addb5"],["/static/media/IncendioEstrada.0e1b5f02.jpg","0e1b5f025589463fa75695884a8f8001"],["/static/media/LixoFlorestas.9349888b.jpg","9349888b4a25a2f0e3e00bf277c7b5f4"],["/static/media/colegas.d55bb838.png","d55bb838c93fd440db28da2a1de14584"],["/static/media/indo_fire_1.aa6bc4b7.jpg","aa6bc4b7cac63f8f4dc53028c0c81c33"]],cacheName="sw-precache-v3-sw-precache-webpack-plugin-"+(self.registration?self.registration.scope:""),ignoreUrlParametersMatching=[/^utm_/],addDirectoryIndex=function(e,t){var n=new URL(e);return"/"===n.pathname.slice(-1)&&(n.pathname+=t),n.toString()},cleanResponse=function(t){return t.redirected?("body"in t?Promise.resolve(t.body):t.blob()).then(function(e){return new Response(e,{headers:t.headers,status:t.status,statusText:t.statusText})}):Promise.resolve(t)},createCacheKey=function(e,t,n,a){var r=new URL(e);return a&&r.pathname.match(a)||(r.search+=(r.search?"&":"")+encodeURIComponent(t)+"="+encodeURIComponent(n)),r.toString()},isPathWhitelisted=function(e,t){if(0===e.length)return!0;var n=new URL(t).pathname;return e.some(function(e){return n.match(e)})},stripIgnoredUrlParameters=function(e,n){var t=new URL(e);return t.hash="",t.search=t.search.slice(1).split("&").map(function(e){return e.split("=")}).filter(function(t){return n.every(function(e){return!e.test(t[0])})}).map(function(e){return e.join("=")}).join("&"),t.toString()},hashParamName="_sw-precache",urlsToCacheKeys=new Map(precacheConfig.map(function(e){var t=e[0],n=e[1],a=new URL(t,self.location),r=createCacheKey(a,hashParamName,n,/\.\w{8}\./);return[a.toString(),r]}));function setOfCachedUrls(e){return e.keys().then(function(e){return e.map(function(e){return e.url})}).then(function(e){return new Set(e)})}self.addEventListener("install",function(e){e.waitUntil(caches.open(cacheName).then(function(a){return setOfCachedUrls(a).then(function(n){return Promise.all(Array.from(urlsToCacheKeys.values()).map(function(t){if(!n.has(t)){var e=new Request(t,{credentials:"same-origin"});return fetch(e).then(function(e){if(!e.ok)throw new Error("Request for "+t+" returned a response with status "+e.status);return cleanResponse(e).then(function(e){return a.put(t,e)})})}}))})}).then(function(){return self.skipWaiting()}))}),self.addEventListener("activate",function(e){var n=new Set(urlsToCacheKeys.values());e.waitUntil(caches.open(cacheName).then(function(t){return t.keys().then(function(e){return Promise.all(e.map(function(e){if(!n.has(e.url))return t.delete(e)}))})}).then(function(){return self.clients.claim()}))}),self.addEventListener("fetch",function(t){if("GET"===t.request.method){var e,n=stripIgnoredUrlParameters(t.request.url,ignoreUrlParametersMatching),a="index.html";(e=urlsToCacheKeys.has(n))||(n=addDirectoryIndex(n,a),e=urlsToCacheKeys.has(n));var r="/index.html";!e&&"navigate"===t.request.mode&&isPathWhitelisted(["^(?!\\/__).*"],t.request.url)&&(n=new URL(r,self.location).toString(),e=urlsToCacheKeys.has(n)),e&&t.respondWith(caches.open(cacheName).then(function(e){return e.match(urlsToCacheKeys.get(n)).then(function(e){if(e)return e;throw Error("The cached response that was expected is missing.")})}).catch(function(e){return console.warn('Couldn\'t serve response for "%s" from cache: %O',t.request.url,e),fetch(t.request)}))}});