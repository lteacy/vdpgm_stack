function val = log_sum_exp(x,dim,y);
% function val = log_sum_exp(x,dim);
% function val = log_sum_exp(x,dim,y);
%
% val = log( sum( exp(x), dim ) )
% val = log( sum( exp(x).*y, dim ) )
%
% x can be -inf but cannot be +inf.

% if has_inf(x)
%   warning(['x contains inf; x=' num2str(x)])
% end
% if has_nan(x)
%   x
%   error('x has NaN(s).')
% end

[x_max, i] = max(x, [], dim);
dims = ones(1, ndims(x));
dims(dim) = size(x, dim);
x = x - repmat(x_max, dims);
val = x_max + log(sum(exp(x), dim));

% Local Variables: ***
% mode: matlab ***
% End: ***
