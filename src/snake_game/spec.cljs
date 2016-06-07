(ns snake-game.spec
    (:require  [cljs.spec :as s]
     [clojure.test.check.generators :as gen]))

(defn only-one [[n1 n2 & xs]]
  (if (not (nil? n2))
    (if (< (Math/abs (- n1 n2)) 2)
      (cons true (only-one (cons n2 xs)))
      [false])
    []))

(defn kropp? [{:keys [kropp]}]
  (let [xs (map first kropp)
        ys (map second kropp)]
    (and (every? identity (only-one xs))
         (every? identity (only-one ys)))))

(s/def ::naturlig-tall (s/and integer? #(< -1 %)))
(s/def ::x (s/and ::naturlig-tall #(< -1 % 35)))
(s/def ::y (s/and ::naturlig-tall #(< 0 % 25)))
(s/def ::koordinat-system  (s/tuple ::naturlig-tall ::naturlig-tall))
(s/def ::punkt (s/spec (s/tuple  ::x  ::y)))
(s/def ::brett ::koordinat-system)
(s/def ::retning (s/and (s/tuple #{-1 0 1} #{-1 0 1}) (fn [[x y]] (not= x y))))
(s/def ::kropp (s/coll-of ::punkt []))
(s/def ::skatt  ::punkt)
(s/def ::poeng (s/and integer? #(< -1 %)))
(s/def ::er-spillet-igang? #{true false})

(s/def ::slange (s/and (s/keys :req-un [::retning ::kropp]) kropp?))

(s/def ::spill (s/keys :req-un [::brett ::slange ::skatt ::poeng ::er-spillet-igang?]))


(s/fdef snake-game.utils/er-det-en-kollisjon?
        :args (s/cat ::slange ::spill)
        :ret #{true false})
#_(s/conform ::slange (:slange  snake-game.utils/nytt-spill))

#_(s/conform ::retning [0 1])

#_(s/explain ::slange (:slange  snake-game.utils/nytt-spill))
#_(s/describe ::slange)
#_(clojure.repl/doc slange)
(gen/generate (s/gen ::naturlig-tall))
(gen/generate (s/gen ::x))
(gen/generate (s/gen ::y))
(gen/generate (s/gen ::punkt))
(gen/generate (s/gen ::retning))
(gen/generate (s/gen ::koordinat-system))

#_(s/conform ::spill nytt-spill)
#_(s/explain ::spill nytt-spill)


(s/fdef snake-game.utils/alle-plasser-på-brettet
        :args (s/cat :brett ::brett)
        :ret (s/coll-of ::punkt []))

(s/fdef snake-game.utils/alle-ledige-plasser
        :args (s/cat :slangens-plasser (s/coll-of ::punkt []) :ledige-plassser (s/coll-of ::punkt []))
        :ret (s/coll-of ::punkt []))

(s/fdef snake-game.utils/finn-en-tilfeldig-ledig-plass-på-brettet
        :args (s/cat :slange ::slange :brett ::brett)
        :ret ::punkt)

(s/fdef snake-game.utils/er-det-en-kollisjon?
        :args (s/cat :slange ::slange :brett ::brett)
        :ret #{true false})

(s/fdef snake-game.utils/slange-halen
        :args (s/cat :punkt ::punkt)
        :ret integer?)

(s/fdef snake-game.utils/gjør-slangen-større
        :args (s/cat :slange ::slange)
        :fn #(= (inc (-> % :args :slange :kropp count)) (-> % :ret :slange :kropp count))
        :ret ::slange)

(s/fdef snake-game.utils/har-vi-truffet-skatten?
        :args (s/cat :slange ::slange :skatt ::punkt)
        :ret #{true false})

(s/fdef snake-game.utils/utfør-flytt
        :args (s/cat :spill ::spill)
        :ret ::spill)

(s/fdef snake-game.utils/avslutt
        :args (s/cat :spill ::spill)
        :ret ::spill)

(s/fdef snake-game.utils/lag-ny-slange
        :args (s/cat :slange ::slange :nytt-hode ::punkt)
        :fn #(and (= (-> % :args :slange :kropp count) (-> % :ret :kropp count))
                  (= (-> % :args :slange :kropp first) (-> % :ret :kropp second))
                  (= (-> % :args :slange :retning) (-> % :ret :retning)))
        :ret ::slange)

(s/fdef snake-game.utils/lag-nytt-hode
        :args (s/cat :slange ::slange)
        :ret ::punkt)

(s/fdef snake-game.utils/flytt-slangen
        :args (s/cat :spill ::spill)
        :ret ::spill)

(s/fdef snake-game.utils/neste-steg
        :args (s/cat :spill ::spill)
        :ret ::spill)

(s/fdef snake-game.utils/oppdater-spill
        :args (s/cat :spill ::spill :foo keyword?)
        :ret ::spill)

(s/fdef snake-game.utils/bytt-retning-på-slangen
        :args (s/cat :ny-retning ::retning :gammel-retning ::retning)
        :ret ::retning)

(s/fdef snake-game.utils/endre-retning
        :args (s/cat :spill ::spill :event (s/tuple (s/and keyword? #(= :endre-retning %)) ::retning))
        :ret ::spill)

(s/instrument #'snake-game.utils/endre-retning)
(s/instrument #'snake-game.utils/lag-ny-slange)
