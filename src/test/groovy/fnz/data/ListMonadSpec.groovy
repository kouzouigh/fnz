package fnz.data

import static fnz.Fnz.bind
import static fnz.Fnz.Just
import static ListMonad.list

import spock.lang.Specification

/**
 *
 */
class ListMonadSpec extends Specification {

    void 'using fmap'() {
        given: 'a list of numbers'
            ListMonad<Integer> fa = list(1,2,3,4)
        when: 'incrementing their value'
            ListMonad<Integer> fb = fa.fmap { it + 1 }
        then: 'result should be the expected'
            fb instanceof ListMonad
            fb.typedRef.value == [2,3,4,5]
    }

    void 'using fmap with an empty list'() {
        given: 'a list of numbers'
            ListMonad<Integer> fa = list()
        when: 'incrementing their value'
            ListMonad<Integer> fb = fa.fmap { it + 1 }
        then: 'result should be the expected'
            fb instanceof ListMonad
            fb.typedRef.value == []
    }

    void 'using fapply'() {
        given: 'a list of numbers'
            ListMonad<Integer> fa = list(1,2,3,4)
        when: 'incrementing their value'
            ListMonad<Integer> fb = fa.fapply(Just({ x -> x + 1 } as Function))
        then: 'result should be the expected'
            fb instanceof ListMonad
            fb.typedRef.value == [2,3,4,5]
    }

    void 'using fapply with an empty list'() {
        given: 'a list of numbers'
            ListMonad<Integer> fa = list()
        when: 'incrementing their value'
            ListMonad<Integer> fb = fa.fapply(Just({ x -> x + 1 } as Function))
        then: 'result should be the expected'
            fb instanceof ListMonad
            fb.typedRef.value == []
    }

    void 'using bind'() {
        given: 'a list of numbers'
            ListMonad<Integer> fa = list(1,2,3,4)
            Function<Integer,ListMonad<Integer>> fn = { Integer i ->
                return list(i + 1)
            }
        when: 'binding with a increment function'
            ListMonad<Integer> result = fa.bind(fn)
        then: 'checking result'
            result.typedRef.value == [2,3,4,5]
    }

    void 'using bind for list comprehensions (I)'() {
        given: 'a list of numbers'
            ListMonad<Integer> fa = list("hi","bye")
        and: 'making bind to look like Haskell bind'
            fa.metaClass.'>>=' = { fn -> delegate.bind(fn) }
        and: 'creating a function containing a list monad'
            def wordAndCount = { String w -> list(w, w.length()) }
        when: 'executing the binding'
            def result = fa.'>>=' wordAndCount
        then: 'we should get the word length and vowels in both words'
            result.typedRef.value == list("hi",2,"bye",3).typedRef.value
    }

    void 'using bind for list comprehensions (II)'() {
        given: 'a list of numbers'
            ListMonad<Integer> fa = list("hi","bye")
        and: 'making bind to look like Haskell bind'
            fa.metaClass.'>>=' = { fn -> delegate.bind(fn) }
        and: 'creating a function containing a list monad'
            def wordAndCount = { String w -> list(w, w.length()) }
        when: 'executing the binding'
            def result = fa.'>>=' wordAndCount
        then: 'we should get the word length and vowels in both words'
            result.typedRef.value == list("hi",2,"bye",3).typedRef.value
    }

    void 'using bind for an empty list'() {
        given: 'an empty list'
            ListMonad<Integer> numbers = list()
        when: 'trying to operate over the list'
            def result = numbers.bind { x -> x + 1 }
        then: 'the list remains empty'
            !numbers.typedRef.value
    }

    void 'using bind for a list with null'() {
        given: 'an empty list'
            ListMonad<Integer> numbers = list(null)
        when: 'trying to operate over the list'
            def result = numbers.bind { x -> x + 1 }
        then: 'the list remains empty'
            !numbers.typedRef.value
    }

    // tag::listmonadvsplaingroovy1[]
    void 'Comparing list monad with plain Groovy (I)'() {
        when: 'collecting number, its double and its half with plain Groovy'
            def result1 =
                (1..3)
                    .collect { x -> [x, x * 2, x.div(2)] }
                    .flatten()
        and: 'with the list monad'
            def result2 =
                list(1..3)
                    .bind { x -> list(x, x * 2, x.div(2)) }
        then: 'all results should give the same result'
            result1 == [1,2,0.5,2,4,1,3,6,1.5]
            result1 == result2.typedRef.value
    }
    // end::listmonadvsplaingroovy1[]

    void 'testing toString()'() {
        expect: 'a string representation'
            list.toString() == expectedRep
        where: 'possible different types of values are'
            list     | expectedRep
            list()   | '[]'
            list(1)  | '[1]'
    }

    void 'testing OR value'() {
        when: 'establishing an alternative to a possible empty list'
            def result = source | alternative
        then: 'we should get the expected value'
            result.typedRef.value == expected.typedRef.value
        where: 'sources and alternatives are'
            source    |alternative |expected
            list(1,2) |list(1)     |list(1,2)
            list()    |list(1)     |list(1)
    }

    void 'testing OR computation'() {
        when: 'establishing an alternative to a possible empty list'
            def result = source | { alternative }
        then: 'we should get the expected value'
            result.typedRef.value == expected.typedRef.value
        where: 'sources and alternatives are'
            source    |alternative |expected
            list(1,2) |list(1)     |list(1,2)
            list()    |list(1)     |list(1)
    }

    void 'testing bind with type awareness and unit'() {
        when: 'using a valid expression'
            TypeAwareFunction<Integer,ListMonad<Integer>> fn = { clazz, value ->
                return clazz.unit(value + 1)
            }
            ListMonad<Integer> possible = options.bind2(fn)
        then: 'the result should have the type'
            possible instanceof ListMonad
        and: 'we should get the expected value'
            val(possible) == expected
        where: 'possible options are'
            options | expected
            list(1) | [2]
            list()  | []
    }

}
