module java::lang::Runnable
  def to_proc
    proc { self.run }
  end
end

module java::lang::Iterable
  include Enumerable

  def each
    iter = iterator
    yield(iter.next) while iter.hasNext
  end

  def each_with_index
    index = 0
    iter = iterator
    while iter.hasNext
      yield(iter.next, index)
      index += 1
    end
  end
end

module java::lang::Comparable
  include Comparable
  def <=>(a)
    return nil if a.nil?
    compareTo(a)
  end
end

class java::lang::Throwable
  def backtrace
    @backtrace ||= stack_trace.map(&:to_s)
  end

  def set_backtrace(trace)
    unless trace.kind_of?(Array) && trace.all? {|x| x.kind_of?(String)}
      raise TypeError.new("backtrace must be an Array of String")
    end
    @backtrace = trace
  end

  def to_s
    message
  end

  def to_str
    to_s
  end

  def inspect
    to_string
  end

  class << self
    alias :old_eqq :===
    def ===(rhs)
      if (NativeException == rhs.class) && (java_class.assignable_from?(rhs.cause.java_class))
        true
      else
        old_eqq(rhs)
      end
    end
  end
end