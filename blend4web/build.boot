(def +lib-version+ "16.11")
(def +version+ (str +lib-version+ "-1"))

(set-env!
  :resource-paths #{"resources"}
  :dependencies '[[cljsjs/boot-cljsjs "0.5.2"]])

(require '[cljsjs.boot-cljsjs.packaging :refer :all]
         '[boot.task.built-in :refer :all])

(task-options!
 pom  {:project     'cljsjs/blend4web
       :version     +version+
       :description "Blend4Web -- Javascript WebGL Framework by Triump LLC"
       :url         "http://www.blend4web.org/"
       :license     {"GPL3" "https://github.com/TriumphLLC/Blend4Web/blob/master/license/GPL-license.txt"}
       :scm {:url   "https://github.com/cljsjs/packages"}})

; (deftask uranium
;   "Ensure uranium.js and uranium.js.mem are in target directory.  This is not
;   needed to build the package, rather its for any projects using the package."
;   []
;   ;;uranium.js and uranium.js.mem need to both be in the same subdir as b4w's target html.
;   (sift :add-jar {'cljsjs/blend4web #"^*/uranium.js*"}
;         :move {#"^*cljsjs/blend4web/common/uranium.js"
;                "uranium.js"}
;         :to-resource #{#"uranium.js*"}))

(deftask package []
  (comp
   (download :url (format "https://raw.githubusercontent.com/TriumphLLC/Blend4Web/%s/deploy/apps/common/b4w.min.js" +lib-version+))
   (download :url (format "https://raw.githubusercontent.com/TriumphLLC/Blend4Web/%s/deploy/apps/common/b4w.simple.min.js" +lib-version+))

   (download :url (format "https://raw.githubusercontent.com/TriumphLLC/Blend4Web/%s/tools/closure-compiler/extern_fullscreen.js" +lib-version+))
   (download :url (format "https://raw.githubusercontent.com/TriumphLLC/Blend4Web/%s/tools/closure-compiler/extern_modules.js" +lib-version+))
   (download :url (format "https://raw.githubusercontent.com/TriumphLLC/Blend4Web/%s/tools/closure-compiler/extern_gl-matrix.js" +lib-version+))
   (download :url (format "https://raw.githubusercontent.com/TriumphLLC/Blend4Web/%s/tools/closure-compiler/extern_pointerlock.js" +lib-version+))

   (download :url (format "https://raw.githubusercontent.com/TriumphLLC/Blend4Web/%s/deploy/apps/common/uranium.js" +lib-version+))
   (download :url (format "https://raw.githubusercontent.com/TriumphLLC/Blend4Web/%s/deploy/apps/common/uranium.js.mem" +lib-version+))

   (sift :move {;;These are all the externs
                #"^extern_fullscreen.js" "cljsjs/blend4web/common/extern_fullscreen.ext.js"
                #"^extern_gl-matrix.js"  "cljsjs/blend4web/common/extern_gl-matrix.ext.js"
                #"^extern_jquery-1.9.js" "cljsjs/blend4web/common/extern_pointerlock.ext.js"
                #"^extern_modules.js"    "cljsjs/blend4web/common/extern_modules.ext.js"

                #"^b4w.simple.min.js" "cljsjs/blend4web/development/b4w.inc.js"
                #"^b4w.min.js"        "cljsjs/blend4web/production/b4w.inc.min.js"

                ;You need uranium these last two in the same file as your html
                ;entry point.
                #"^uranium.js"     "cljsjs/blend4web/common/uranium.js"
                #"^uranium.js.mem" "cljsjs/blend4web/common/uranium.js.mem"})

   (sift      :include   #{#"^cljsjs"})
   (deps-cljs :name     "blend4web")
   (pom)
   (jar)))