(ns udelcoursetrack.core
  (:gen-class)
  (:require [net.cgrand.enlive-html :as html]
            [org.httpkit.client :as http]))

(require '[postal.core :refer [send-message]])

;;the throw-away email address and password used to send the alerts
(def email "udcoursetrack@gmail.com")
(def pass "pass-word")

(def conn {:host "smtp.gmail.com"
           :ssl true
           :user email
           :pass pass})

(defn get-test-domain
  "for testing purposes only"
  []
  ;;add a test file here of scraped course contents
  (read-string (slurp "/home/carl/acct200010")))

(def lasttrack "")
(def nextlasttrack "")

(defn get-dom2
  "scrape the contents of the URL depending on the course (variant 2)"
  [course]
  (html/html-snippet
   @(http/get (str "https://udapps.nss.udel.edu/CoursesSearch/search-results?&term=2178&search_type=A&course_sec=" course "&text_info=All&credit=Any&session=All&instrtn_mode=All&startat=2178" nextlasttrack) {:insecure? true})))

(defn get-dom
  "scrape the contents of the URL depending on the course (variant 1)"
  [course]
  (html/html-snippet
   @(http/get (str "https://primus.nss.udel.edu/CoursesSearch/search-results?term=2178&search_type=A&course_sec=" course "&session=All&course_title=&instr_name=&text_info=All&instrtn_mode=All&time_start_hh=&time_start_ampm=&credit=Any&keyword=&subj_area_code=&college=") {:insecure? true})))


(defn get-next-dom
  "scrape the contents of the URL depending on the course (including next page(s))"
  [course]
  (concat (get-dom course) 
          (html/html-snippet
           @(http/get (str "https://udapps.nss.udel.edu/CoursesSearch/search-results?&term=2178&search_type=A&course_sec=" course "&text_info=All&credit=Any&session=All&instrtn_mode=All&startat=2178" lasttrack) {:insecure? true}))
          (html/html-snippet
                            @(http/get (str "https://udapps.nss.udel.edu/CoursesSearch/search-results?&term=2178&search_type=A&course_sec=" course "&text_info=All&credit=Any&session=All&instrtn_mode=All&startat=2178" nextlasttrack) {:insecure? true}))))

(defn in? 
  "true if the value is in coll"
  [coll value]  
  (some #(= value %) coll))

(defn cycleprint
  "adds elements to a new sequence if the criteria is met"
  [sequence] 
  (loop [iteration 0 newseq []]
    (if (= iteration (count sequence))
      newseq
      (recur (inc iteration)
             ;;criteria
             (if (in? (map :class (map :attrs (nth sequence iteration))) "\\\"coursenum\\\"")
               (if ((complement in?) newseq (filter (complement nil?) (map :content (nth sequence iteration))))
                   (do 
                     (into newseq 
                           ;;class code and class type
                           [(filter (complement nil?) (map :content (nth sequence iteration)))
                            ;;class name
                            (clojure.string/replace (nth (nth sequence (+ iteration 1)) 0) #"\\n                  " "")
                            ;;open seats
                            (clojure.string/replace (clojure.string/replace (nth (nth sequence (+ iteration 2)) 0) #"\\n                  " "") #"\\n               " "")
                            ;;currently full or not
                            (if (= (filter (complement nil?) (map :content (nth sequence (+ iteration 2)))) '()) "seats available" "currently full")
                            ;;duration of course
                            (clojure.string/replace (clojure.string/replace (nth (nth sequence (+ iteration 3)) 0) #"\\n                  " "") #"\\n               " "")
                            ;;days
                            (clojure.string/replace (nth (nth sequence (+ iteration 4)) 0) #"[\\n ]" "")
                            ;;exam days
                            ;;clojure.string/replace (nth (nth sequence (+ iteration 4)) 2) #"[\\n ]" "")
                            ;;time
                            (clojure.string/replace (nth (nth sequence (+ iteration 5)) 0) #"[\\n ]" "")
                            ;;exam time
                            ;;(clojure.string/replace (nth (nth sequence (+ iteration 5)) 2) #"[\\n ]" "")
                            ;;class and exam location
                            (filter (complement nil?) (map :content (nth sequence (+ iteration 6))))
                            ;;instructor
                            (clojure.string/replace (clojure.string/replace (clojure.string/replace (clojure.string/replace (nth (nth sequence (+ iteration 7)) 0) #"\\n                  " "") #"\\n               " "") #"         " "") #"      " "")
                            ])) (into newseq nil)) 
               (into newseq nil))))))

(def prompt "")

(defn controlledinput
  "prompts for a valid course name"
  []
  (def ct (do (println "Please type in the course you want to track and press enter") (read-line)))
  (def check (map #(nth % 0) (map first (take-nth 9 (cycleprint (filter (complement nil?) (map :content (get-next-dom ct))))))))
  (if (> (count check) 1) (do (println (str "Here are the list of courses available, " (apply str (interleave check (repeat ", "))) "please narrow down your search")) (controlledinput))
      (if (= (count check) 0) (do (println "0 matches found, please search again") (controlledinput)) ct)))



(defn defrefresh
  "refresh local definitions of scraped data"
  []
  (def relevantlist1 (filter (complement nil?) (map :content (get-dom ct))))


  ;;for relevantlist, use (get-domx) for testing, otherwise use to scrape from website (get-next-dom ct)
  (def relevantlist (filter (complement nil?) (map :content (get-next-dom ct))))

  (def relevantlist2 (filter (complement nil?) (map :content (get-dom ct))))
  (def generate-coursesx (cycleprint relevantlist))
  
  (def emptylist? #(= % '()))

  (def number-of-coursesx (count (take-nth 9 (cycleprint relevantlist))))
  
  (def coursesx1 (map #(nth % 0) (map first (take-nth 9 (cycleprint relevantlist1)))))
  (def coursesx (map #(nth % 0) (map first (take-nth 9 (cycleprint relevantlist)))))
  
  (def typesx (map #(nth % 0) (map second (take-nth 9 (cycleprint relevantlist)))))
  (def course-typesx (map vector coursesx typesx))
  
  (def namesx (take-nth 9 (drop 1 (cycleprint relevantlist))))
  (def course-namesx (map vector coursesx namesx))
  
  (def seatsx (take-nth 9 (drop 2 (cycleprint relevantlist))))
  (def course-seatsx (map vector coursesx seatsx))
  
  (def full-or-notx (take-nth 9 (drop 3 (cycleprint relevantlist))))
  (def course-full-or-notx (map vector coursesx full-or-notx))
  
  (def durationx (take-nth 9 (drop 4 (cycleprint relevantlist))))
  (def course-durationx (map vector coursesx durationx))
  
  (def daysx (take-nth 9 (drop 5 (cycleprint relevantlist))))
  (def course-daysx (map vector coursesx daysx))

  (def timesx (take-nth 9 (drop 6 (cycleprint relevantlist))))
  (def course-timesx (map vector coursesx timesx))

  (def roomx (map #(nth % 0) (map first (take-nth 9 (drop 7 (cycleprint relevantlist))))))
  (def course-roomx (map vector coursesx roomx))


  (def instructorsx (take-nth 9 (drop 8 (cycleprint relevantlist))))
  (def course-instructorsx (map vector coursesx instructorsx))


  (def course-infox (map vector coursesx typesx namesx seatsx full-or-notx durationx daysx timesx roomx instructorsx))
  (def course-infox2 (first (map vector coursesx typesx namesx seatsx full-or-notx durationx daysx timesx roomx instructorsx)))

  (def subjectformat (str "Your course " (first coursesx) " has seats available!"))

  (def bodyformat (str "Your course " (first course-infox2) " has seats available (" (nth course-infox2 3) " LEFT) with professor " (last course-infox2) " from " (nth course-infox2 7) " on " (nth course-infox2 6) " in room " (nth course-infox2 8)))

  )

(defrefresh)


(defn checkcontains
  "update and list course information vector"
  []
  (defrefresh)
  course-infox)








(defn sendmail
  "send mail to the prompted email address"
  [] 
  (send-message conn {:from email
                      :to prompt
                      :subject subjectformat
                      :body bodyformat}))

(defn repeatevery [task duration] 
  (future (while true (do (Thread/sleep duration) (task)))))


(declare stopnow)
(declare trackvar)

(defn stoptrackcondition
  "checks for seat availability within the course information vector until a seat is found"
  []
  (if (= (apply #(nth % 4) (checkcontains)) "seats available") (do (future-cancel trackvar) (println "Free seats are available! Sending email...") (defrefresh) (sendmail)) (stoptrackcondition)))



(defn begintracking
  "update course information definitions and call the checker"
  []
  (defrefresh)
   (def trackvar (repeatevery #(println (checkcontains)) 1000))
  (Thread/sleep 1000)
  (stoptrackcondition))




(defn coursetrack
  "obtain user email address and valid course entry and start tracking"
  []
  (def prompt (do (println "Enter your email address and press enter") (read-line)))
  (controlledinput)
  (begintracking))







(defn -main []
  (defrefresh)
  (coursetrack)
  )

